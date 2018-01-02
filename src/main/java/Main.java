import java.lang.annotation.*;

@Target({ ElementType.TYPE_USE })
@interface TypeUseAnnotation {
}

abstract class Base<T> {
	abstract T m();
}

class Impl extends Base<Void> {
	Void m() {
		@TypeUseAnnotation Object o = new Object();
		return null;
	}
}


