package io.druid.indexing.overlord;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;

public class PortFinderTest
{
  private final PortFinder finder = new PortFinder(1200);

  @Test
  public void testUsedPort() throws IOException
  {
    final int port1 = finder.findUnusedPort();
    // verify that the port is free
    ServerSocket socket1 = new ServerSocket(port1);
    finder.markPortUnused(port1);
    final int port2 = finder.findUnusedPort();
    Assert.assertNotEquals("Used port is not reallocated", port1, port2);
    // verify that port2 is free
    ServerSocket socket2 = new ServerSocket(port2);

    socket1.close();
    // Now port1 should get recycled
    Assert.assertEquals(port1, finder.findUnusedPort());

    socket2.close();
    finder.markPortUnused(port1);
    finder.markPortUnused(port2);

  }
}