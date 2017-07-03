package pkd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class History {
	
	protected List<byte[]> _history = new ArrayList<byte[]>();
	
	public void add(byte[] key){
		_history.add(key);
	}
	
	public boolean check(byte[] key){
		for(int i = 0; i != _history.size(); i++){
			if(Arrays.equals(key, _history.get(i))){
				return false;
			}
		}
		return true;
	}
}
