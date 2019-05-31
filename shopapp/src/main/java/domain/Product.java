package domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@Entity
@NamedQueries({
	@NamedQuery(name="product.all", query="Select p From Product p"),
	@NamedQuery(name="product.id", query="Select p From Product p Where p.id=:productId"),
	@NamedQuery(name="product.name", query="Select p From Product p Where p.name Like '%:productName%'"),
	@NamedQuery(name="product.category", query="Select p From Product p Where p.category=:productCategory"),
	@NamedQuery(name="product.price", query="Select p From Product p Where p.price Between :from And :to"),
	@NamedQuery(name="product.comments", query="Select c.content From Comment c Where c.product.id=:productId "),
	@NamedQuery(name="product.comment.id", query="Select c From Comment c Where c.id=:commentId and c.product.id=:productId")
})
public class Product {

	public enum Category {
		GPU, MOBO, HDD, RAM
	}
	
	@Column(name="Id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String name;
	private String brand;
	private Category category;
	private double price;
	private List<Comment> comments = new ArrayList<Comment>();
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	
	@XmlTransient
	@OneToMany(mappedBy="product")
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
}
