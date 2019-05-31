package rest;

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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import domain.Comment;
import domain.Product;

@Path("/product")
@Stateless
public class ProductResources {

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
		result.setName(p.getName());
		result.setBrand(p.getBrand());
		result.setCategory(p.getCategory());
		if (p.getPrice() > 0)
		result.setPrice(p.getPrice());
		em.persist(result);
		return Response.ok().build();
	}
	
	@GET
    @Path("/price")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> getItemsByPriceRange(@QueryParam("from") double from, @QueryParam("to") double to) {
        return em.createNamedQuery("product.price", Product.class).setParameter("from", from).setParameter("to", to).getResultList();
    }
	
	@GET
	@Path("/{id}/comment")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Comment> getComments(@PathParam("id") int id){
		return em.createNamedQuery("product.comments", Comment.class)
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
		result.getComments().add(comment);
		comment.setProduct(result);
		em.persist(comment);
		return Response.ok().build();
	}
	@DELETE
	@Path("/{id}/comment/delete/{comId}")
	public Response deleteComment(@PathParam("id") int id, @PathParam("comId") int comId) {
		Product result = em.createNamedQuery("product.comment.id", Product.class)
				.setParameter("productId", id)
				.setParameter("commentId", comId)
				.getSingleResult();
		if(result==null)
			return Response.status(404).build();
		em.remove(result);
		return Response.ok().build();
	}
	
}
