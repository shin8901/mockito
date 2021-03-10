package com.sds.cleancode.restaurant;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EmailValidatorTest {

    @Test
    public void 이메일에_골뱅이가없으면_false를리턴한다(){
    }

    @Test
    public void 이메일에_특수문자가있으면_false를리턴한다(){
    }

    @Test
    public void 이메일의_골뱅이뒤에_도메인주소가없으면_false를리턴한다(){
    }

    @Test
    public void 이메일이_유효하면_true를리턴한다(){
    }
}