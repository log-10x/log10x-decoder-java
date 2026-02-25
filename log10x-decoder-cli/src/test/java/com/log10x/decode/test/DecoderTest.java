package com.log10x.decode.test;

import java.io.IOException;

import com.log10x.decode.SingleEventDecoder;
import com.log10x.decode.lexer.TokenizeContext;
import com.log10x.decode.options.EventEncodeOptions;
import com.log10x.decode.options.TemplateEncodeOptions;
import com.log10x.decode.options.TimestampOptions;
import com.log10x.decode.options.TokenOptions;

public class DecoderTest {

	public static void main(String[] args) throws IOException {
		
		String hash = "$bvKCbEl6WYQ";
		String line = "1441120604000 Paths yxsu ImageNet ILSVRC2012 00000622 JPEG 0 135395 00000669 153355";
		String pattern = "$(yy/MM/dd HH:mm:ss) INFO rdd.BinaryFileRDD: Input split: $://$//dataset//$//$_img_test//$1_test_$.$:$+$,//$7//dataset//$6//$5_img_test//$5_test_$.$4:$3+$";
		
		System.out.println(SingleEventDecoder.decode(context(), hash, line, pattern));
	}

	private static TokenizeContext context() {
		return TokenizeContext.create(
			new TokenOptions(),
			new TimestampOptions(),
			new TemplateEncodeOptions(),
			new EventEncodeOptions());
	}
}
