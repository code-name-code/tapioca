package hr.codenamecode.tapioca.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class URIsTest {

  @Test
  public void should_strip_leading_slash() {
    assertEquals("a", URIs.stripLeadingSlash("/a"));
    assertEquals("", URIs.stripLeadingSlash(""));
  }

  @Test
  public void should_strip_trailing_slash() {
    assertEquals("a", URIs.stripTrailingSlash("a/"));
    assertEquals("", URIs.stripTrailingSlash("/"));
  }

  @Test
  public void should_strip_slashes() {
    assertEquals("a", URIs.stripSlashes("/a/"));
    assertEquals("", URIs.stripSlashes("/"));
  }
}
