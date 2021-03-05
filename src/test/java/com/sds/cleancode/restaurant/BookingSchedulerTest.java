package com.sds.cleancode.restaurant;

import static org.assertj.core.api.BDDAssertions.then;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Test;

public class BookingSchedulerTest {

	public static final Customer CUSTOMER = new Customer("user-name", "010-1234-5678");
	public static final int CAPACITY_PER_HOUR_THREE = 3;
	public static final int NUMBER_OF_PEOPLE_FOR_BOOKING = 2;
	public BookingScheduler bookingScheduler = new BookingScheduler(CAPACITY_PER_HOUR_THREE);
	public static final DateTime ON_THE_HOUR = new DateTime(2021, 1, 1, 11, 0);

	@Test(expected = RuntimeException.class)
	public void 예약은_정시에만_가능하다_정시가_아닌경우_예약불가() {
		//given
		DateTime notOnTheHour = new DateTime(2021, 1,1,11,10);
		Schedule schedule = new Schedule(notOnTheHour, NUMBER_OF_PEOPLE_FOR_BOOKING, CUSTOMER);

		//when
		bookingScheduler.addSchedule(schedule);

		//then
	}

	@Test
	public void 예약은_정시에만_가능하다_정시인_경우_예약가능() {
		//given
		Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_BOOKING, CUSTOMER);

		//when
		bookingScheduler.addSchedule(schedule);

		//then
		then(bookingScheduler.hasSchedule(schedule)).isTrue();
		//assertThat(bookingScheduler.hasSchedule(schedule), is(true));
	}

	@Test
	public void 시간대별_인원제한이_있다_같은_시간대에_Capacity_초과할_경우_예외발생() {
		//given
		Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_BOOKING, CUSTOMER);
		bookingScheduler.addSchedule(schedule);

		//when
		try {
			bookingScheduler.addSchedule(schedule);
		} catch (Exception e) {
			//then
			assertThat(e.getMessage(), is("Number of people is over restaurant capacity per hour"));
		}
	}

	@Test
	public void 시간대별_인원제한이_있다_같은_시간대가_다르면_Capacity_차있어도_스케쥴_추가_성공() {
		//given
		Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE_FOR_BOOKING, CUSTOMER);
		bookingScheduler.addSchedule(schedule);

		//when
		DateTime differentOnTheHour = ON_THE_HOUR.plusHours(1);
		Schedule newSchedule = new Schedule(differentOnTheHour, NUMBER_OF_PEOPLE_FOR_BOOKING, CUSTOMER);
		bookingScheduler.addSchedule(newSchedule);

		//then
		assertThat(bookingScheduler.hasSchedule(newSchedule), is(true));
	}

	@Test
	public void 예약완료시_SMS는_무조건_발송() {
	}

	@Test
	public void 이메일이_없는_경우에는_이메일_미발송() {
	}

	@Test
	public void 이메일이_있는_경우에는_이메일_발송() {
	}

	@Test
	public void 현재날짜가_일요일인_경우_예약불가_예외처리() {
	}

	@Test
	public void 현재날짜가_일요일이_아닌경우_예약가능() {
	}
}
