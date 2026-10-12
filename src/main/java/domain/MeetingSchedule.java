package domain;

import java.util.Objects;

public record MeetingSchedule<T extends Number>(T start, T end, Class<T> type) {

	public MeetingSchedule {
		Objects.requireNonNull(start);
		Objects.requireNonNull(end);
		Objects.requireNonNull(type);
		var msg = "start must be before end.";

		switch (type.getTypeName()) {

		case String s when s.contains(Double.class.getSimpleName()):

			if (start.doubleValue() > end.doubleValue()) {
				throw new IllegalArgumentException(msg);
			}

			break;
		case String s when s.contains(Integer.class.getSimpleName()):
			if (start.intValue() > end.intValue()) {
				throw new IllegalArgumentException(msg);
			}
			break;
		case String s when s.contains(Long.class.getSimpleName()):
			if (start.longValue() > end.longValue()) {
				throw new IllegalArgumentException(msg);
			}
			break;
		default:
			break;
		}

	}

}