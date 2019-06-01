package rest;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import domain.Comment;
import domain.Product;
import domain.Product.Category;

@Path("/product")
@Stateless
public class ProductResources {
	
	public static int currentId = 1;

	@PersistenceContext
	EntityManager em;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Product> getAll() {
		return em.createNamedQuery("product.all", Product.class).getResultList();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Add(Product product) {
		if (product.getCategory() == null)
            return Response.status(400).entity("bad category").build();
        if (product.getName() == null)
            return Response.status(400).entity("bad name").build();
        if (product.getPrice() < 0.0)
            return Response.status(400).entity("bad price").build();
        em.persist(product);
        return Response.status(201).build();
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") int id){
		Product result = em.createNamedQuery("product.id", Product.class)
				.setParameter("productId", id)
				.getSingleResult();
		if(result==null){
			return Response.status(404).build();
		}
		return Response.ok(result).build();
	}
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("id") int id, Product p){
		Product result = em.createNamedQuery("product.id", Product.class)
				.setParameter("productId", id)
				.getSingleResult();
		if(result==null){
			return Response.status(404).build();
		}
		if (p.getName() != null)
			result.setName(p.getName());
		if (p.getBrand() != null)
			result.setBrand(p.getBrand());
		if (p.getCategory() != null)
			result.setCategory(p.getCategory());
		if (p.getPrice() > 0)
			result.setPrice(p.getPrice());
		em.persist(result);
		return Response.ok().build();
	}
	
	@GET
    @Path("/price/{from}/{to}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> getItemsByPriceRange(@PathParam("from") double from, @PathParam("to") double to) {
        return em.createNamedQuery("product.price", Product.class).setParameter("from", from).setParameter("to", to).getResultList();
    }
	
	@GET
    @Path("/name/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> getItemsByName(@PathParam("name") String name) {
        return em.createNamedQuery("product.name", Product.class).setParameter("productName", "%"+name+"%").getResultList();
    }
	
	@GET
    @Path("/category/{category}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> getItemsByCategory(@PathParam("category") Category category) {
        return em.createNamedQuery("product.category", Product.class).setParameter("productCategory", category).getResultList();
    }
	
	@GET
	@Path("/{id}/comment")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Comment> getComments(@PathParam("id") int id){
		return em.createNamedQuery("product.id.comments", Comment.class)
				.setParameter("productId", id)
				.getResultList();
	}
	@POST
	@Path("/{id}/comment")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addComment(@PathParam("id") int id, Comment comment){
		Product result = em.createNamedQuery("product.id", Product.class)
				.setParameter("productId", id)
				.getSingleResult();
		if(result==null)
			return Response.status(404).build();
		if (result.getComments() == null)
        	result.setComments(new ArrayList<Comment>());
        if (comment.getContent() == null)
            return Response.status(400).entity("no content in comment").build();
        comment.setId(++currentId);
		result.getComments().add(comment);
		comment.setProduct(result);
		em.persist(comment);
		return Response.ok().build();
	}
	@DELETE
	@Path("/{id}/comment/delete")
	public Response deleteComment(@PathParam("id") int id, Product p) {
		Product result = em.createNamedQuery("product.id", Product.class)
				.setParameter("productId", id)
				.getSingleResult();
		String tmpName = result.getName();
		String tmpBrand = result.getBrand();
		String tmpCategory = result.getCategory().toString();
		double tmpPrice = result.getPrice();
		
		Comment dropResult = em.createNamedQuery("product.id.comments", Comment.class)
				.setParameter("productId", id)
				.getSingleResult();
		em.remove(dropResult);
		
		em.createNativeQuery("Insert Into Product (id, name, brand, category, price) Values (?, ?, ?, ?, ?)")
		.setParameter(1, id)
		.setParameter(2, tmpName)
		.setParameter(3, tmpBrand)
		.setParameter(4, tmpCategory)
		.setParameter(5, tmpPrice)
		.executeUpdate();
		
		
		em.persist(p);
		return Response.ok().build();
	}	
}
