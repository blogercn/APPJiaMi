package com.dzm.apkqudao;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class Test {

	private static final String DES = "DES/CBC/PKCS5Padding";
	
	private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey securekey = keyFactory.generateSecret(dks);
        IvParameterSpec iv = new IvParameterSpec(key);
        
        Cipher cipher = Cipher.getInstance(DES);
        cipher.init(Cipher.ENCRYPT_MODE, securekey, iv);
        return cipher.doFinal(data);
    }
	
	private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey securekey = keyFactory.generateSecret(dks);
        IvParameterSpec iv = new IvParameterSpec(key);
        
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);
        // 用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, iv);
        return cipher.doFinal(data);
    }
	
	/**
     * 将二进制转换成16进值制 ，防止byte[]数字转换成string类型时造成的数据损失
     * @param buf 
     * @return 返回16进制转换成的string
     */ 
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
 
    }
 
    /**
     * 将16进制转换为二进制
     * @param hexStr 16进制的数组转换成String类型再传过来的参数
     * @return 转换回来的二进制数组
     */ 
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
                    16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
    public static String toHexString1(byte[] b){
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < b.length; ++i){
            buffer.append(toHexString1(b[i]));
        }
        return buffer.toString();
    }
    public static String toHexString1(byte b){
        String s = Integer.toHexString(b & 0xFF);
        if (s.length() == 1){
            return "0" + s;
        }else{
            return s;
        }
    }

	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	private static int toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}
	public static void main(String[] args) throws Exception {
		byte[] data = "0123456789".getBytes("utf-8");//getBytes("ASCII");//.getBytes("utf-8");
		byte[] key = "abcdefgh".getBytes("utf-8");//("ASCII");//("utf-8");
		byte[] result = encrypt(data, key);
	    
		System.out.println("result = "+parseByte2HexStr(result));
		System.out.println("result = "+toHexString1(result));
		System.out.println("data = ");
		for(byte b : data){
			System.out.print(0xFF & b);
			System.out.print(" ");
		}
		System.out.println();
		System.out.println("key = ");
		for(byte b : key){
			System.out.print(0xFF & b);
			System.out.print(" ");
		}
		System.out.println();
		System.out.println("result = ");
		for(byte b : result){
			System.out.print(0xFF & b);
			System.out.print(" ");
		}
		
		byte[] bb = decrypt(parseHexStr2Byte("9577EA1C831C2AC5D3C5381E014B78C5"), key);
		System.out.println();
		System.out.println("bb = ");
		for(byte b : bb){
			System.out.print(0xFF & b);
			System.out.print(" ");
		}
		
		byte[] cc = decrypt(hexStringToByte("9577EA1C831C2AC5D3C5381E014B78C5"), key);
		System.out.println();
		System.out.println("cc = ");
		for(byte b : cc){
			System.out.print(0xFF & b);
			System.out.print(" ");
		}
	}
}
