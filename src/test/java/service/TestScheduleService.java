package service;

import static java.time.LocalDate.now;
import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import domain.MeetingSchedule;

class TestScheduleService {

	private List<MeetingSchedule> meetingSchedules;

	@BeforeEach
	void init() {

		meetingSchedules = new ArrayList<>();

	}

	@Test
	@DisplayName("Time integration LocalDateTime. Long parameterization.")
	void test_time() {

		var start = now().atTime(14, 30).toEpochSecond(UTC);

		var end = now().atTime(15, 30).toEpochSecond(UTC);

		var expEnd = now().atTime(16, 30).toEpochSecond(UTC);

		var t1 = new MeetingSchedule<>(start, end, Long.class);
		var t2 = new MeetingSchedule<>(start, expEnd, Long.class);
		meetingSchedules.add(t1);
		meetingSchedules.add(t2);
		meetingSchedules.add(t2);

		var resolvedSchedules = ScheduleService.resolveScheduleOverlaps.apply(meetingSchedules);
		assertEquals(1, resolvedSchedules.size());
		var tk = resolvedSchedules.getFirst();
		assertEquals(start, tk.start());
		assertEquals(expEnd, tk.end());

	}

	@Test
	@DisplayName("IllegalArgumentException expected as start parameter > end parameter. Long parameterization.")
	void test_exp_exc_long_param() {

		assertThrows(IllegalArgumentException.class, () -> {

			new MeetingSchedule<>(4L, 3L, Long.class);
		});
	}

	@Test
	@DisplayName("No overlaps in meeting schedule. Integer parameterization.")
	void test_integer_param_talk() {

		var t1 = new MeetingSchedule<>(1, 3, Integer.class);
		var t2 = new MeetingSchedule<>(3, 4, Integer.class);
		var t3 = new MeetingSchedule<>(4, 6, Integer.class);
		meetingSchedules.add(t1);
		meetingSchedules.add(t2);
		meetingSchedules.add(t3);

		assertEquals(meetingSchedules.size(), ScheduleService.resolveScheduleOverlaps.apply(meetingSchedules).size());
	}

	@Test
	@DisplayName("No overlaps in meeting schedule. Double parameterization.")
	void test_no_overlap() {

		var t1 = new MeetingSchedule<>(1.5, 3.5, Double.class);
		var t2 = new MeetingSchedule<>(3.9, 4d, Double.class);
		var t3 = new MeetingSchedule<>(4.5, 6.5, Double.class);
		meetingSchedules.add(t1);
		meetingSchedules.add(t2);
		meetingSchedules.add(t3);

		assertEquals(meetingSchedules.size(), ScheduleService.resolveScheduleOverlaps.apply(meetingSchedules).size());
	}

	@Test
	@DisplayName("Single overlap in meeting schedules.")
	void test_single_overlap_max() {

		var t1 = new MeetingSchedule<>(1.5, 3d, Double.class);
		var t2 = new MeetingSchedule<>(3d, 4d, Double.class);
		var t3 = new MeetingSchedule<>(4d, 5d, Double.class);
		var t4 = new MeetingSchedule<>(4d, 6d, Double.class);
		meetingSchedules.add(t1);
		meetingSchedules.add(t2);
		meetingSchedules.add(t2);
		meetingSchedules.add(t3);
		meetingSchedules.add(t4);

		var resolvedSchedules = ScheduleService.resolveScheduleOverlaps.apply(meetingSchedules);
		assertEquals(3, resolvedSchedules.size());

		var tk = resolvedSchedules.getLast();
		assertEquals(t4, tk);
		assertEquals(4d, tk.start());
		assertEquals(6d, tk.end());

	}

	@Test
	@DisplayName("IllegalArgumentException expected as start parameter > end parameter. Double parameterization.")
	void test_exp_exc_double_param() {

		assertThrows(IllegalArgumentException.class, () -> {

			new MeetingSchedule<>(4.5, 3.5, Double.class);
		});
	}

	@Test
	@DisplayName("IllegalArgumentException as start parameter > end parameter. Integer parameterization.")
	void test_exp_exc_integer_param() {

		assertThrows(IllegalArgumentException.class, () -> {

			new MeetingSchedule<>(4, 3, Integer.class);
		});
	}

}
