package com.santa.cleancode.restaurant;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class BookingSchedulerWithMockTest {

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
