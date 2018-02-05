package com.lc.async;

import java.util.List;

/**
 * Created by lc on 2017/7/14.
 */
public interface EventHandler {
    void doHandle(EventModel model);
    List<EventType> getSupportEventTypes();
}
