import java.io.*;
import java.nio.file.Files;
import java.util.zip.InflaterInputStream;

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    //System.out.println("Logs from your program will appear here!");

    final String command = args[0];

    switch (command) {
      case "init" -> {
        final File root = new File(".git");
        new File(root, "objects").mkdirs();
        new File(root, "refs").mkdirs();
        final File head = new File(root, "HEAD");

        try {
          head.createNewFile();
          Files.write(head.toPath(), "ref: refs/heads/main\n".getBytes());
          System.out.println("Initialized git directory");
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
      case "cat-file" -> {
        final String argument = args[1];
        final String blobHash = args[2];
        if (argument.equals("-p")) {
          File blobFile = new File(".git/objects/" + blobHash.substring(0, 2) + "/" + blobHash.substring(2));
          if (blobFile.exists()) {
            try (InputStream is = new InflaterInputStream(new FileInputStream(blobFile));
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
              byte[] buffer = new byte[1024];
              int length;
              while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
              }
              String blob = baos.toString();
              String content = blob.substring(blob.indexOf('\0') + 1);
              System.out.write(content.getBytes());
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }
        }
      }
      default -> System.out.println("Unknown command: " + command);
    }
  }
}