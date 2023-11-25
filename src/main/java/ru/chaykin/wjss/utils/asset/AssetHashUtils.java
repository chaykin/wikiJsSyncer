package ru.chaykin.wjss.utils.asset;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

import lombok.NoArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import ru.chaykin.wjss.data.asset.IAsset;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class AssetHashUtils {

    public static String md5PageHash(IAsset asset) {
	try (InputStream content = asset.getContent()) {
	    MessageDigest digest = DigestUtils.getMd5Digest();
	    DigestUtils.updateDigest(digest, content);
	    DigestUtils.updateDigest(digest, asset.getRemotePath());
	    return Hex.encodeHexString(digest.digest());
	} catch (IOException e) {
	    throw new RuntimeException("Could not calculate md5 hash", e);
	}
    }
}
