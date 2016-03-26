package la.aquare.spinver.test;
import static org.junit.Assert.assertTrue;
import la.aquare.spinver.Post;
import la.aquare.spinver.lang.dict.Actions.Action;

import org.junit.Before;
import org.junit.Test;

public class Buy {
	Post post;
	
	@Before
	public void setup(){
		 //empty
	}
	
	@Test
	public void Positive() {
//		assertTrue(Post.process("Compro Iphone 5s") == Action.BUY);
//		assertTrue(Post.process("To comprando Iphone 5s") == Action.BUY);
//		assertTrue(Post.process("Quero comprar ou vender elefante branco") == Action.BUY);
		
		Action result = Post.processTest("Quero comprar ou vender elefante branco");
		assertTrue(result == Action.BUY || result == Action.BOTH);
//		assertTrue(Post.process("Onde sua compra é 100% SEGURA, aproveite!") == Action.BUY);
	}

}
