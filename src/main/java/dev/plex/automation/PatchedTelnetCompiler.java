package dev.plex.automation;

import com.google.common.collect.ImmutableList;
import dev.plex.BukkitTelnetModule;
import dev.plex.util.PlexLog;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class PatchedTelnetCompiler {
    private static final PluginManager PLUGIN_MANAGER = Bukkit.getPluginManager();
    private static final URI CODE_ARCHIVE = URI.create("https://github.com/plexusorg/BukkitTelnet/archive/refs/heads/master.zip");
    private static final Path PLUGIN_DIRECTORY = Bukkit.getServer().getPluginsFolder().toPath();
    private static final File TARGET_PLUGIN = PLUGIN_DIRECTORY.resolve("BukkitTelnet.jar").toFile();
    private static final Path WORKING_DIRECTORY = Path.of(System.getProperty("user.dir"));
    private static final Path ROOT_PATH = WORKING_DIRECTORY.resolve("build");
    private static final Path EXTRACT_TARGET = ROOT_PATH.resolve("extract");
    private static final Path EXTRACT_SUBDIR = EXTRACT_TARGET.resolve("BukkitTelnet-master");
    private static final Path BINARIES_PATH = EXTRACT_SUBDIR.resolve("build").resolve("libs");
    private static final String DOWNLOADED_ARCHIVE_PATH = String.valueOf(ROOT_PATH.resolve("BukkitTelnet-master.zip"));
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public static void execute() throws Exception {
        // Create directories
        final List<Path> directories = ImmutableList.of(ROOT_PATH, EXTRACT_TARGET);

        for (Path directory : directories) {
            PlexLog.debug("Checking if {0} exists...", String.valueOf(directory));
            if (Files.notExists(directory)) {
                PlexLog.debug("It doesn't! Creating directory...");
                Files.createDirectory(directory);
            }
        }

        downloadArchive();
    }

    private static void downloadArchive() throws Exception {
        PlexLog.log("Downloading archive...");
        // Create the request
        final HttpRequest request = HttpRequest.newBuilder()
                .GET()
                // GitHub may block blank/generic user agents in the future, so we're spoofing one
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.41 Safari/537.36")
                .uri(CODE_ARCHIVE)
                .build();

        // Send the request
        final HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        // Get the redirect
        final URI redirect = URI.create(response.headers().firstValue("location").orElseThrow());

        // Download the file from the redirect
        final HttpRequest downloadRequest = HttpRequest.newBuilder()
                .GET()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.41 Safari/537.36")
                .uri(redirect)
                .build();

        HTTP_CLIENT.send(downloadRequest, HttpResponse.BodyHandlers.ofFileDownload(ROOT_PATH, StandardOpenOption.CREATE, StandardOpenOption.WRITE));

        extractArchive();
    }

    private static void extractArchive() throws Exception {
        PlexLog.log("Extracting archive...");
        final ZipInputStream inputStream = new ZipInputStream(new FileInputStream(DOWNLOADED_ARCHIVE_PATH));
        ZipEntry entry = inputStream.getNextEntry();

        while (entry != null) {
            final Path outputDestination = EXTRACT_TARGET.resolve(entry.getName());

            if (entry.isDirectory()) {
                if (Files.notExists(outputDestination)) {
                    PlexLog.debug("{0} doesn't exist, creating it!", String.valueOf(outputDestination));
                    Files.createDirectory(outputDestination);
                }
            } else {
                final FileOutputStream outputStream = new FileOutputStream(String.valueOf(outputDestination));
                int read = 0;
                while ((read = inputStream.read()) != -1) {
                    outputStream.write(read);
                }

                outputStream.close();
            }

            inputStream.closeEntry();
            entry = inputStream.getNextEntry();
        }

        executeGradleTarget();
    }

    private static void executeGradleTarget() throws Exception {
        PlexLog.log("Executing gradle target...");
        boolean nix = !System.getProperty("os.name").toLowerCase().contains("win"); // Assume Windows if name contains win

        String gradlew = String.valueOf(nix ? EXTRACT_SUBDIR.resolve("gradlew") : EXTRACT_SUBDIR.resolve("gradlew.bat"));
        if (nix) {
            final ProcessBuilder chmodBuilder = new ProcessBuilder("chmod", "+x", gradlew);
            Process chmodProcess = chmodBuilder.start();
            chmodProcess.waitFor();
        }

        final ProcessBuilder builder = new ProcessBuilder(gradlew, "--no-daemon", "clean", "build");
        builder.directory(new File(String.valueOf(EXTRACT_SUBDIR)));
        PlexLog.debug("Executing compile command: {0}", builder.command());

        //builder.redirectErrorStream(true);
        //builder.redirectOutput(ProcessBuilder.Redirect.PIPE);
        builder.inheritIO();

        final Process process = builder.start();
        process.waitFor();
        PlexLog.debug("Compilation command ended with status code {0}", process.exitValue());
        copyBinary();
    }

    private static void copyBinary() throws Exception {
        PlexLog.log("Copying binaries...");
        final File binaryDirectory = new File(String.valueOf(BINARIES_PATH));
        final File[] files = binaryDirectory.listFiles();

        if (files == null) {
            throw new IllegalStateException("Didn't manage to compile jars!");
        } else if (files.length == 0) {
            throw new IllegalStateException("Didn't manage to compile jars!");
        }

        Files.copy(BINARIES_PATH.resolve("BukkitTelnet.jar"), TARGET_PLUGIN.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Files.walk(ROOT_PATH)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);

        Timer timer = new Timer();

        final Plugin plugin = PLUGIN_MANAGER.loadPlugin(TARGET_PLUGIN);
        if (plugin == null) throw new IllegalStateException("BukkitTelnet cannot be null after successful compile!");

        plugin.onLoad();
        PLUGIN_MANAGER.enablePlugin(plugin);
        done();
    }

    private static void done() {
        BukkitTelnetModule.getModule().enable();
    }
}
