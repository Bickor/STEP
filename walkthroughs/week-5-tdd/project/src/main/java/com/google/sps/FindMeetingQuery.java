// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<TimeRange> possibilities;
    Collection<String> attendees = request.getAttendees();
    Collection<String> optionalAttendees = request.getOptionalAttendees();

    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
        possibilities = Arrays.asList();
        return possibilities;
    }

    if ((request.getDuration() < TimeRange.WHOLE_DAY.duration() && request.getAttendees().isEmpty()) || events.isEmpty()) {
        possibilities = Arrays.asList(TimeRange.WHOLE_DAY);
        return possibilities;
    }

    List<TimeRange> eventsAL = new ArrayList<>();
    possibilities = new ArrayList<>();
    for (Event event : events) {
        if (!Collections.disjoint(event.getAttendees(), attendees)) {
            eventsAL.add(event.getWhen());
        }
    }

    List<TimeRange> openSpaces = openSlots(eventsAL);
    System.out.println(openSpaces);

    for (int i = 0; i < openSpaces.size(); i++) {
        if (openSpaces.size() == 1 && openSpaces.get(i).duration() == TimeRange.WHOLE_DAY.duration() - 1) {
            possibilities.add(TimeRange.fromStartEnd(0, TimeRange.WHOLE_DAY.duration(), false));
            return possibilities;
        }

        if (openSpaces.get(i).duration() >= request.getDuration()) {
            possibilities.add(TimeRange.fromStartEnd(openSpaces.get(i).start(), openSpaces.get(i).end(), false));
        }
    }

    return possibilities;
  }

  /**
   * Get the free slots on the day.
   */
  public List<TimeRange> openSlots(List<TimeRange> events) {
      System.out.println(events);
      List<TimeRange> possibilities;

      // If there are no events.
      if (events.isEmpty()) {
          return Arrays.asList(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.END_OF_DAY, false));
      }

      possibilities = new ArrayList<>();

      // Get first event
      if (events.get(0).start() > TimeRange.START_OF_DAY) {
          possibilities.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, events.get(0).start(), false));
      }

      // Get the space between each event.
      for (int i = 0; i < events.size() - 1; i++) {
          if (events.get(i + 1).start() - events.get(i).end() > 0) {
              possibilities.add(TimeRange.fromStartEnd(events.get(i).end(), events.get(i + 1).start(), false));
          }
      }

      // Include space between last event and the end of the day.
      if (events.get(events.size() - 1).end() <= TimeRange.END_OF_DAY) {
          possibilities.add(TimeRange.fromStartEnd(events.get(events.size() - 1).end(), TimeRange.END_OF_DAY, true));
      }

      return possibilities;
  }
}
