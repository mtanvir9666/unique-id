package com.scalable.service;

import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/*
+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
|U|        EPOCH          |   NODE_ID  |  COUNTER  |
+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+

U(unused): 1 bit
EPOCH = 41 bits
NODE_ID = 12 bits
COUNTER = 12 bits
*/

@Component
public class UniqueIdService {
  private static final int UNUSED_LENGTH = 1;
  private static final int EPOCH_LENGTH = 41;
  private static final int NODE_ID_LENGTH = 10;
  private static final int COUNTER_LENGTH = 12;

  private static final long MAX_COUNTER_VALUE = (1L << COUNTER_LENGTH) - 1;

  // Should get it from a coordinator service during bootup
  private long nodeId = 24;

  private long currentTs = -1L;
  private long previousTs = -1L;
  private long counter = 0L;

  private long generateNextCounter() {
    // New milli second. Reset the counter to zero
    if (currentTs > previousTs) {
      counter = 0;
      return 0;
    }
    // Clock has turned backward -- don't generate ID
    if (currentTs < previousTs) {
      throw new ResponseStatusException(
          HttpStatus.NO_CONTENT,
          "Not possible to generate a ID for this time. Clock has turned backward)");
    }

    if (currentTs == previousTs) {
      counter = (counter + 1) % MAX_COUNTER_VALUE;
      if (counter == 0) {
        throw new ResponseStatusException(
            HttpStatus.NO_CONTENT,
            "Not possible to generate a ID for this time. Counter reset withing a millisecond");
      }
    }
    return counter;
  }

  public long nextId() {
    currentTs = Instant.now().toEpochMilli();
    counter = generateNextCounter();
    long id = currentTs << (NODE_ID_LENGTH + COUNTER_LENGTH) |
              nodeId << COUNTER_LENGTH |
              counter;
    previousTs = currentTs;
    return id;
  }
}
