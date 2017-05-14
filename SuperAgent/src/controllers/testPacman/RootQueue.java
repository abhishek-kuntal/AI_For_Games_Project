package controllers.testPacman;

import java.util.*;

public class RootQueue<T> {
	private List<T> data = new ArrayList<T>();
	private List<T> parent = new ArrayList<T>();
	private List<Integer> depth = new ArrayList<Integer>();

	public boolean enqueue(T inputData, T parentData, int depthData) {
		data.add(inputData);
		parent.add(parentData);
		depth.add(depthData);
		return true;
	}

	// ���̉�3�̓Z�b�g�Ŏg�����ƁB

	public T dequeue() {
		T outputData = null;
		outputData = data.get(0);
		data.remove(0);
		return outputData;
	}

	public T getParent() {
		T output = null;
		output = parent.get(0);
		parent.remove(0);
		return output;
	}

	public int getDepth() {
		int output;
		output = depth.get(0);
		depth.remove(0);
		return output;
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}
}
