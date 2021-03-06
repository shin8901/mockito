package com.sds.cleancode.restaurant;

import static org.assertj.core.api.BDDAssertions.then;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

public class BookingSchedulerTest {

	public static final DateTime NOT_ON_THE_HOUR = new DateTime(2021, 1, 1, 10, 30);
	public static final Customer CUSTOMER = new Customer("user-name", "010-1234-5678");
	public static final DateTime ON_THE_HOUR = new DateTime(2021, 1, 1, 10, 0);
	public static final int NUMBER_OF_PEOPLE_FOR_TABLE = 2;
	public static final int CAPACITY_PER_HOUR = 3;

	public BookingScheduler bookingScheduler;
	public TestableSmsSender testableSmsSender = new TestableSmsSender();
	public TestableMailSender testableMailSender = new TestableMailSender();

	@Before
	public void setUp() {
		bookingScheduler = new BookingScheduler(CAPACITY_PER_HOUR);
		bookingScheduler.setSmsSender(testableSmsSender);
		bookingScheduler.setMailSender(testableMailSender);
	}

	@Test(expected = RuntimeException.class)
	public void 예약은_정시에만_가능하다_정시가_아닌경우_예약불가() {
		//given
		Schedule schedule = new Schedule(NOT_ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);

		//when
		bookingScheduler.addSchedule(schedule);

		//then
	}

	@Test
	public void 예약은_정시에만_가능하다_정시인_경우_예약가능() {
		//given
		Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);

		//when
		bookingScheduler.addSchedule(schedule);

		//then
		//assertThat(bookingScheduler.hasSchedule(schedule), is(true));
		then(bookingScheduler.hasSchedule(schedule)).isTrue();
	}

	@Test
	public void 시간대별_인원제한이_있다_같은_시간대에_Capacity_초과할_경우_예외발생() {
		//given
		Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);
		bookingScheduler.addSchedule(schedule);

		//when
		try {
			bookingScheduler.addSchedule(schedule);
		} catch (RuntimeException e) {
			//then
			then(e.getMessage()).isEqualTo("Number of people is over restaurant capacity per hour");
		}
	}

	@Test
	public void 시간대별_인원제한이_있다_같은_시간대가_다르면_Capacity_차있어도_스케쥴_추가_성공() {
		//given
		Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);
		bookingScheduler.addSchedule(schedule);

		//when
		DateTime differentHour = ON_THE_HOUR.plusHours(1);
		Schedule newSchedule = new Schedule(differentHour, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);
		bookingScheduler.addSchedule(newSchedule);

		//then
		then(bookingScheduler.hasSchedule(newSchedule)).isTrue();
	}

	@Test
	public void 예약완료시_SMS는_무조건_발송() {
		//given
		Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);

		//when
		bookingScheduler.addSchedule(schedule);

		//then
		then(testableSmsSender.isSendMethodCalled()).isTrue();
	}

	@Test
	public void 이메일이_없는_경우에는_이메일_미발송() {
		//given
		Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, CUSTOMER);

		//when
		bookingScheduler.addSchedule(schedule);

		//then
		then(testableMailSender.getSendMethodCallCount()).isEqualTo(0);
	}

	@Test
	public void 이메일이_있는_경우에는_이메일_발송() {
		//given
		Customer customerWithEmail = new Customer("user-name", "010-1234-5678", "email@google.com");
		Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_TABLE, customerWithEmail);

		//when
		bookingScheduler.addSchedule(schedule);

		//then
		then(testableMailSender.getSendMethodCallCount()).isEqualTo(1);
	}

	@Test
	public void 현재날짜가_일요일인_경우_예약불가_예외처리() {
	}

	@Test
	public void 현재날짜가_일요일이_아닌경우_예약가능() {
	}
}
