package cum.jesus.cts.util;

import java.util.function.Consumer;

public class Variant2<A, B> {
    private Object value = null;
    private byte type = -1;

    public void setA(A a) {
        value = a;
        type = 0;
    }

    public void setB(B b) {
        value = b;
        type = 1;
    }

    public void consume(Consumer<A> aConsumer, Consumer<B> bConsumer) {
        if (type == 0) {
            aConsumer.accept((A) value);
        } else if (type == 1) {
            bConsumer.accept((B) value);
        } else {
            throw new RuntimeException("Unset variant");
        }
    }
}
