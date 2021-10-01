package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entities.Department;

public class DepartmentSevice {
	
	public List<Department> findAll(){
		List<Department> list = new ArrayList<>();
		list.add(new Department(1, "computers"));
		list.add(new Department(2, "Books"));
		list.add(new Department(3, "Eletronics"));
		return list;
	}

}
