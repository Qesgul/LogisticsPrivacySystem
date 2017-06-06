package com.example.rsa_encrypt;

public class StrAndByteTrans {
	private static final char[] bcdLookup = { '0', '1', '2', '3', '4', '5',
		'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	/**
	* Transform the specified byte into a Hex String form.
	*/
	public static final String bytesToHexStr(byte[] bcd) 
	{
		StringBuffer s = new StringBuffer(bcd.length * 2);

		for (int i = 0; i < bcd.length; i++) 
		{
			s.append(bcdLookup[(bcd[i] >>> 4) & 0x0f]);
			s.append(bcdLookup[bcd[i] & 0x0f]);
		}

		return s.toString();
	}

	/**
	* Transform the specified Hex String into a byte array.
	*/
	public static final byte[] hexStrToBytes(String s)
	{
	   byte[] bytes;
	   bytes = new byte[s.length() / 2];
	   for (int i = 0; i < bytes.length; i++)
	   {
	     bytes[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2),16);
	   }
	   return bytes;
	}
}
