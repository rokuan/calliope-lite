package apps.rokuan.com.calliope_helper_lite.util;

import scala.Function1;

/**
 * Created by LEBEAU Christophe on 19/03/2017.
 */

public abstract class SimpleFunction<T, R> implements Function1<T, R> {
    @Override
    public <A> Function1<A, R> compose(Function1<A, T> function1) {
        return null;
    }

    @Override
    public <A> Function1<T, A> andThen(Function1<R, A> function1) {
        return null;
    }

    @Override
    public boolean apply$mcZD$sp(double v) {
        return false;
    }

    @Override
    public double apply$mcDD$sp(double v) {
        return 0;
    }

    @Override
    public float apply$mcFD$sp(double v) {
        return 0;
    }

    @Override
    public int apply$mcID$sp(double v) {
        return 0;
    }

    @Override
    public long apply$mcJD$sp(double v) {
        return 0;
    }

    @Override
    public void apply$mcVD$sp(double v) {

    }

    @Override
    public boolean apply$mcZF$sp(float v) {
        return false;
    }

    @Override
    public double apply$mcDF$sp(float v) {
        return 0;
    }

    @Override
    public float apply$mcFF$sp(float v) {
        return 0;
    }

    @Override
    public int apply$mcIF$sp(float v) {
        return 0;
    }

    @Override
    public long apply$mcJF$sp(float v) {
        return 0;
    }

    @Override
    public void apply$mcVF$sp(float v) {

    }

    @Override
    public boolean apply$mcZI$sp(int i) {
        return false;
    }

    @Override
    public double apply$mcDI$sp(int i) {
        return 0;
    }

    @Override
    public float apply$mcFI$sp(int i) {
        return 0;
    }

    @Override
    public int apply$mcII$sp(int i) {
        return 0;
    }

    @Override
    public long apply$mcJI$sp(int i) {
        return 0;
    }

    @Override
    public void apply$mcVI$sp(int i) {

    }

    @Override
    public boolean apply$mcZJ$sp(long l) {
        return false;
    }

    @Override
    public double apply$mcDJ$sp(long l) {
        return 0;
    }

    @Override
    public float apply$mcFJ$sp(long l) {
        return 0;
    }

    @Override
    public int apply$mcIJ$sp(long l) {
        return 0;
    }

    @Override
    public long apply$mcJJ$sp(long l) {
        return 0;
    }

    @Override
    public void apply$mcVJ$sp(long l) {

    }
}
