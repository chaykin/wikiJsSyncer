package ru.chaykin.wjss.utils.page;

import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import ru.chaykin.wjss.data.page.IPage;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class PageHashUtils {

    public static String md5PageHash(IPage page) {
	return DigestUtils.md5Hex(page.getContent() + page.getRemotePath() + page.getTags());
    }
}
