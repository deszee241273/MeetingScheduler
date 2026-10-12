package service;

import static java.time.LocalDate.now;
import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import domain.MeetingSchedule;

class TestScheduleService {

	@Test
	@DisplayName("LocalDateTime, Long parameterization.")
	void test_time() {

		var start = now().atTime(14, 30).toEpochSecond(UTC);

		var end = now().atTime(15, 30).toEpochSecond(UTC);

		var expEnd = now().atTime(16, 30).toEpochSecond(UTC);

		var t1 = new MeetingSchedule<>(start, end, Long.class);
		var t2 = new MeetingSchedule<>(start, expEnd, Long.class);

		List<MeetingSchedule> meetingSchedules = List.of(t1, t2, t2);

		var resolvedSchedules = ScheduleService.resolveScheduleOverlaps.apply(meetingSchedules);
		assertEquals(1, resolvedSchedules.size());
		var tk = resolvedSchedules.getFirst();
		assertEquals(start, tk.start());
		assertEquals(expEnd, tk.end());

	}

	@Test
	@DisplayName("No overlaps in meeting schedule. Integer parameterization.")
	void test_integer_param_talk() {

		var t1 = new MeetingSchedule<>(1, 3, Integer.class);
		var t2 = new MeetingSchedule<>(3, 4, Integer.class);
		var t3 = new MeetingSchedule<>(4, 6, Integer.class);

		List<MeetingSchedule> meetingSchedules = List.of(t1, t2, t3);

		assertEquals(meetingSchedules.size(), ScheduleService.resolveScheduleOverlaps.apply(meetingSchedules).size());
	}

	@Test
	@DisplayName("No overlaps in meeting schedule. Double parameterization.")
	void test_no_overlap() {

		var t1 = new MeetingSchedule<>(1.5, 3.5, Double.class);
		var t2 = new MeetingSchedule<>(3.9, 4d, Double.class);
		var t3 = new MeetingSchedule<>(4.5, 6.5, Double.class);
		List<MeetingSchedule> meetingSchedules = List.of(t1, t2, t3);

		assertEquals(meetingSchedules.size(), ScheduleService.resolveScheduleOverlaps.apply(meetingSchedules).size());
	}

	@Test
	@DisplayName("Single overlap in meeting schedules.")
	void test_single_overlap_max() {

		var t1 = new MeetingSchedule<>(1.5, 3d, Double.class);
		var t2 = new MeetingSchedule<>(3d, 4d, Double.class);
		var t3 = new MeetingSchedule<>(4d, 5d, Double.class);
		var t4 = new MeetingSchedule<>(4d, 6d, Double.class);
		List<MeetingSchedule> meetingSchedules = List.of(t1, t2, t2, t3, t4);

		var resolvedSchedules = ScheduleService.resolveScheduleOverlaps.apply(meetingSchedules);
		assertEquals(3, resolvedSchedules.size());

		var tk = resolvedSchedules.getLast();
		assertEquals(t4, tk);
		assertEquals(4d, tk.start());
		assertEquals(6d, tk.end());

	}

	@ParameterizedTest
	@MethodSource("testArguments")
	@DisplayName("IllegalArgumentException if start parameter > end parameter")
	void test_exp_exc(Class cls) {

		assertThrows(IllegalArgumentException.class, () -> {

			new MeetingSchedule<>(4L, 3L, cls);
		});
	}

	private static Stream<Arguments> testArguments() {

		return Stream.of(Arguments.of(Long.class), Arguments.of(Double.class), Arguments.of(Integer.class)

		);
	}

}
