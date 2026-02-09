package com.jhs.httpserver.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

public class WebRootHandler {
    private final File webroot;

    public WebRootHandler(String webrootPath) throws WebrootNotFoundException {
        this.webroot = new File(webrootPath);
        try {
            if (!webroot.exists() || !webroot.isDirectory()) {
                throw new WebrootNotFoundException("Le chemin du webroot spécifié est invalide : " + webrootPath);
            }
        } catch (SecurityException e) {
            throw new WebrootNotFoundException("Accès refusé au webroot : " + webrootPath);
        }
    }

    // private helper used by tests via reflection
    private boolean checkIfEndsWithSlash(String path) {
        return path != null && path.endsWith("/");
    }

    // private helper used by tests via reflection
    private boolean checkIfProvidedRelativePathExists(String relativePath) {
        try {
            File f = resolveRelativePath(relativePath);
            return f.exists() && f.isFile();
        } catch (IOException e) {
            return false;
        }
    }

    public String getFileMimeType(String relativePath) throws FileNotFoundException {
        try {
            File f = resolveRelativePath(relativePath);
            if (f.isDirectory()) {
                // assume index.html
                f = new File(f, "index.html");
            }
            if (!f.exists()) throw new FileNotFoundException(relativePath);
            String name = f.getName().toLowerCase();
            if (name.endsWith(".html") || name.endsWith(".htm") || name.equals("index.html")) return "text/html";
            if (name.endsWith(".png")) return "image/png";
            if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
            if (name.endsWith(".css")) return "text/css";
            if (name.endsWith(".js")) return "application/javascript";
            return "application/octet-stream";
        } catch (IOException e) {
            throw new FileNotFoundException(relativePath);
        }
    }

    public byte[] getFileByteArrayData(String relativePath) throws FileNotFoundException, ReadFileException {
        try {
            File f = resolveRelativePath(relativePath);
            if (f.isDirectory()) {
                f = new File(f, "index.html");
            }
            if (!f.exists()) throw new FileNotFoundException(relativePath);
            return Files.readAllBytes(f.toPath());
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) throw (FileNotFoundException)e;
            throw new ReadFileException(e.getMessage(), e);
        }
    }

    private File resolveRelativePath(String relativePath) throws IOException {
        String rel = relativePath == null ? "" : relativePath;
        if (rel.equals("/") || rel.equals("")) {
            return webroot.getCanonicalFile();
        }
        // strip leading slash
        if (rel.startsWith("/")) rel = rel.substring(1);
        File candidate = new File(webroot, rel);
        String webrootCanonical = webroot.getCanonicalPath();
        String candidateCanonical = candidate.getCanonicalPath();
        if (!candidateCanonical.equals(webrootCanonical) && !candidateCanonical.startsWith(webrootCanonical + File.separator)) {
            throw new IOException("Path escapes webroot");
        }
        return candidate;
    }
}
