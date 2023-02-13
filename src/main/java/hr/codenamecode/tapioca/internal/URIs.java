package hr.codenamecode.tapioca.internal;

public class URIs {

  public static String stripLeadingSlash(String path) {
    boolean startsWithSlash = path.startsWith("/");
    int begin = startsWithSlash ? 1 : 0;
    if (startsWithSlash && path.length() == 1) {
      return "";
    }
    return path.substring(begin);
  }

  public static String stripTrailingSlash(String path) {
    boolean endsWithSlash = path.endsWith("/");
    int end = endsWithSlash ? path.length() - 1 : path.length();
    if (endsWithSlash && path.length() == 1) {
      return "";
    }
    return path.substring(0, end);
  }

  public static String stripSlashes(String path) {
    return stripTrailingSlash(stripLeadingSlash(path));
  }
}
