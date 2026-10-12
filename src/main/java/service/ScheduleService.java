package service;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.TreeMap;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import domain.MeetingSchedule;

final class ScheduleService {
	private ScheduleService() {
	}

	private static final BinaryOperator<MeetingSchedule> biop = (t1,
			t2) -> t1.start().longValue() - t2.end().longValue() > 0 ? t1 : t2;

	public static UnaryOperator<List<MeetingSchedule>> resolveScheduleOverlaps = op -> {

		var mergedByStarttimes = op.stream().collect(toMap(MeetingSchedule::start, identity(),

				biop, TreeMap::new));

		return mergedByStarttimes.values().stream().toList();

	};

}
