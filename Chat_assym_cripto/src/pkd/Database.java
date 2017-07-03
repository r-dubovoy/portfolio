package pkd;

import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;;

public class Database {
	
	public class Record {
		
		public String _ip;
		public int    _port;
		public byte[] _pubKeyEncoded;
		
		Record(String ip, int port, byte[] pubKeyEncoded) {
			
			_ip = ip;
			_port = port;
			_pubKeyEncoded = pubKeyEncoded;
		}
	}
	
	//we'll use ip+port as key
	protected LinkedHashMap<String, Record> _hmap = null;
	
	public Database() {
		
		_hmap = new LinkedHashMap<String, Record>();
	}
	
	public void updatePort(int index, int port){
		
		Record rec = get(index);
		rec._port = port;
		
	}
	
	public void add(String ip, int port, byte[] pubKeyEncoded) throws NoSuchAlgorithmException {
		
		_hmap.put(Utilities.host2md5hex(ip, port), new Record(ip, port, pubKeyEncoded));
	}
	
    public void add(byte[] hash, String ip, int port, byte[] pubKeyEncoded) throws NoSuchAlgorithmException {
		
    	System.out.println("Adding rec for "+ Utilities.bytes2hex(hash));
		_hmap.put(Utilities.bytes2hex(hash), new Record(ip, port, pubKeyEncoded));
	}
	
	public byte[] getKey(String ip, int port) throws NoSuchAlgorithmException {
		
		Record rec = _hmap.get(Utilities.host2md5hex(ip, port));
		return rec != null ? rec._pubKeyEncoded : null;
	}
	
	public byte[] getKey(byte[] hash) {
		
		Record rec = _hmap.get(Utilities.bytes2hex(hash));
		return rec != null ? rec._pubKeyEncoded : null;
		
	}
	
	public byte[] getKey(int index) {
		
		Record rec = get(index);
		return rec != null ? rec._pubKeyEncoded : null;
	}
	
	public String getIP(int index) {
		
		Record rec = get(index);
		return rec != null ? rec._ip : null;
	}
	
	public String getIP(byte[] hash) {
		
		Record rec = _hmap.get(Utilities.bytes2hex(hash));
		return rec != null ? rec._ip : null;
	}
	
	public int getPort(int index) {
	
		Record rec = get(index);
		return rec != null ? rec._port : -1;
	}
	
	public int getPort(byte[] hash) {
		
		Record rec = _hmap.get(Utilities.bytes2hex(hash));
		return rec != null ? rec._port : -1;
	}
	
	public byte[] getHash(int index) {
		return  Utilities.hex2bytes((String)(_hmap.keySet().toArray())[ index ]);
	}
	
	public Record get(int index) {
		return _hmap.get( (_hmap.keySet().toArray())[ index ] );
	}
	
	public int size() {
		return _hmap.size();
	}

	public void update(byte[] hash, String ip, int port, byte[] keyEncoded) throws NoSuchAlgorithmException {
	
		Record rec = _hmap.get(Utilities.host2md5hex(ip, port));
		rec._pubKeyEncoded = keyEncoded;
		System.out.println("Updated key for" + Utilities.bytes2hex(hash)); 
	}
	
//	protected String convert2key(String ip, int port) {
//		
//		return ip + Integer.toString(port);
//	}

}
