package captcha;

import java.util.Random;

import com.github.cage.Cage;
import com.github.cage.token.RandomTokenGenerator;

public class MyCage extends Cage {

	public MyCage() {
		super(null, null, null, null, DEFAULT_COMPRESS_RATIO, new RandomTokenGenerator(new Random(), 4), null);
	}
}
