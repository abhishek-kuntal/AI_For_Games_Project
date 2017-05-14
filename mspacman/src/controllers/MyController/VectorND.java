package controllers.MyController;

//package controllers;

public class VectorND {
	public double elements[];
	
	public VectorND(double a[], double ... args) {
		elements = new double [a.length + args.length];
		for(int i = 0; i < a.length; i++){
			elements[i] = a[i];
		}
		for(int i = 0; i < args.length; i++){
			elements[i + a.length] = args[i];
		}
	}
	
	public VectorND(int size){
		elements = new double[size];
	}
	
	public VectorND() {
		elements = null;
	}
	
	public void normallize(){
		for(int i = 0; i < elements.length; i++){
			if (elements[i] > 0) elements[i] = 4;
			else if (elements[i] < 0) elements[i] = -4;
		}
	}
	
	public double len() {
		if(elements == null) return 0;
		double tmp = 0;
		for(int i = 0; i < elements.length; i++){
			tmp += Math.abs(elements[i]);
		}
		return tmp;
	}
	
	public VectorND copy(){
		return new VectorND(elements);
	}
	
	public void maxElVec(VectorND v){
		if(v.elements == null) throw new RuntimeException("Null vector.");
		if(elements == null){
			elements = new double[v.elements.length];
			for (int i = 0; i < elements.length; i++) elements[i] = v.elements[i];
			return;
		}
		for (int i = 0; i < elements.length; i++) elements[i] = (elements[i] > v.elements[i]) ? elements[i] : v.elements[i];
	}
	
	public void minElVec(VectorND v){
		if(v.elements == null) throw new RuntimeException("Null vector.");
		if(elements == null){
			elements = new double[v.elements.length];
			for (int i = 0; i < elements.length; i++) elements[i] = v.elements[i];
			return;
		}
		for (int i = 0; i < elements.length; i++) elements[i] = (elements[i] < v.elements[i]) ? elements[i] : v.elements[i];
	}
	
	static public VectorND sub(VectorND v1, VectorND v2) throws Exception{
		if(v1.elements == null || v2.elements == null) throw new RuntimeException("Null vector.");
		if(v1.elements.length != v2.elements.length) throw new RuntimeException("The size of 2 vectors aren't the same.");
		double tmp[] = new double[v1.elements.length];
		for (int i = 0; i < v1.elements.length; i++) tmp[i] = v1.elements[i] - v2.elements[i];
		return new VectorND(tmp);
	}
	
	static public VectorND add(VectorND v1, VectorND v2) throws Exception{
		if(v1.elements == null || v2.elements == null) throw new RuntimeException("Null vector.");
		if(v1.elements.length != v2.elements.length) throw new RuntimeException("The size of 2 vectors aren't the same.");
		double tmp[] = new double[v1.elements.length];
		for (int i = 0; i < v1.elements.length; i++) tmp[i] = v1.elements[i] + v2.elements[i];
		return new VectorND(tmp);
	}
	
	static public VectorND mul(VectorND v, double k) throws Exception{
		if(v.elements == null) throw new RuntimeException("Null vector.");
		double tmp[] = new double[v.elements.length];
		for (int i = 0; i < v.elements.length; i++) tmp[i] = k*v.elements[i];
		return new VectorND(tmp);
	}
		
	
	public void sub(VectorND v) throws Exception{
		if(v.elements == null) throw new RuntimeException("Null vector.");
		if(elements == null) elements = new double[v.elements.length];
		if(elements.length != v.elements.length) throw new RuntimeException("The size of 2 vectors aren't the same.");
		for (int i = 0; i < elements.length; i++) elements[i] -= v.elements[i];
	}
	
	public void add(VectorND v) throws Exception{
		if(v.elements == null) throw new RuntimeException("Null vector.");
		if(elements == null) elements = new double[v.elements.length];
		if(elements.length != v.elements.length) throw new RuntimeException("The size of 2 vectors aren't the same.");
		for (int i = 0; i < elements.length; i++) elements[i] += v.elements[i];
	}
	
	public void mul(double k) throws Exception{
		if(elements == null) return;
		for (int i = 0; i < elements.length; i++) elements[i] *= k;
	}
	
	public VectorND mul(VectorND v){
		if(v.elements == null) throw new RuntimeException("Null vector.");
		if(elements == null) elements = new double[v.elements.length];
		if(elements.length != v.elements.length) throw new RuntimeException("The size of 2 vectors aren't the same.");
		for (int i = 0; i < elements.length; i++) elements[i] *= v.elements[i];
		return this;
	}
	
	public VectorND div(VectorND v){
		if(v.elements == null) throw new RuntimeException("Null vector.");
		if(elements == null) elements = new double[v.elements.length];
		if(elements.length != v.elements.length) throw new RuntimeException("The size of 2 vectors aren't the same.");
		for (int i = 0; i < elements.length; i++) elements[i] /= (v.elements[i] + 1e-6);
		return this;
	}
}
