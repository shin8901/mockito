# 도메인 소개 - 실습 순서

## Restaurant Booking

* Legacy 코드에 대한 테스트 케이스 작성

    * Mock Framework를 사용하지 않고 테스트 케이스 작성

* 테스트 코드 리팩토링

    * Mock Framework를 사용하여 테스트 케이스 작성

    * 생성자 Mock, InjectMock, Mock을 적용하여 테스트 코드 리팩토링


* Repository URL

    * https://code.sdsdev.co.kr/act-edu/restaurant-booking

## 도메인 소개

* 레스토랑 예약 시스템

* BookingScheduler를 통해 시간대별 예약관리

* 예약은 정시에만 가능하다. → ex. 09:00(0), 09:03(x)

* 시간대별 수용가능 인원을 정할 수 있다.

* 예약완료 시 SMS발송

* 이메일 주소가 있는 경우는 메일 발송

* 일요일은 예약이 불가하다.

![RESTAURANT_BOOKING](./image/03_restaurant_booking.JPG)

```
public class BookingSchedulerTest {

	@Test
	public void 예약은_정시에만_가능하다_정시가_아닌경우_예약불가() {
	}

	@Test
	public void 예약은_정시에만_가능하다_정시인_경우_예약가능() {
	}

	@Test
	public void 시간대별_인원제한이_있다_같은_시간대에_Capacity_초과할_경우_예외발생() {
	}

	@Test
	public void 시간대별_인원제한이_있다_시간대가_다르면_Capacity_차있어도_스케쥴_추가_성공() {
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
```

## 코드 설명

* BookingScheduler

* Customer

* EmailValidator

* MailSender

* Schedule

* SmsSender

```
public class BookingScheduler {
	private int capacityPerHour;	
	private List<Schedule> schedules;	
	private SmsSender smsSender;
	private MailSender mailSender;

	public BookingScheduler(int capacityPerHour) {...}
	
	public void addSchedule(Schedule schedule) {...}

	public boolean hasSchedule(Schedule schedule) { return schedules.contains(schedule);}

	public void setSmsSender(SmsSender smsSender) {	this.smsSender = smsSender;}

	public void setMailSender(MailSender mailSender) { this.mailSender = mailSender;}
}
```

[이전](02_junit.md)  [다음](04_wirte_simple_unit_tests.md)
