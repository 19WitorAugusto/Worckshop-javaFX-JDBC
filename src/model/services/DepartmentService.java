package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {

	private DepartmentDao dao = DaoFactory.createDepartmentDao();

	public List<Department> findAll() {

		return dao.findAll();// vai no banco busca todos os dados!

	}

	public void saveOrUpdate(Department obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		} else {
			dao.update(obj);
		}
	}
}

/*
 * MOCK -> retorna dados de mentira (sem ser do banco)
 * 
 * List<Department> list = new ArrayList<>(); list.add(new Department(1,
 * "Books")); list.add(new Department(2, "Computers")); list.add(new
 * Department(3, "Electronics"));
 * 
 * return list;
 */
