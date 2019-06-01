package domain.services;

import java.util.List;
import java.util.ArrayList;

import domain.Product;

public class ProductService {

	public static List<Product> db = new ArrayList<Product>();
	public List<Product> getAll(){
		return db;
	}
	public Product get(int id){
		for(Product p : db){
			if(p.getId() == id)
				return p;
		}
		return null;
	}
	public void add(Product p){
		p.setId(db.size() + 1);
		db.add(p);
	}
	
	public void delete(Product p) {
        db.remove(p);
    }
	
	public void update(Product product){
		for(Product p : db){
			if(p.getId()==product.getId()){
				p.setName(product.getName());
				p.setBrand(product.getBrand());
				p.setCategory(product.getCategory());
				p.setPrice(product.getPrice());
				p.setComments(product.getComments());
			}
		}
	}
}
