package com.cosmoclub.models;

import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Entity
@Table(name ="usuarios")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String status;
	
	@Size(max = 20, message = "El nombre debe tener un maximo de 20 caracteres.")
	@NotEmpty(message = "El nombre es requerido.")
	@Column(nullable = false)
	private String name;
	
	@Size(max = 20, message = "El apellido debe tener un maximo de 20 caracteres.")
	@NotEmpty(message = "El Apellido es requerido.")
	@Column(nullable = false)
	private String last_name;
	
	@Size(min = 9, max = 60, message = "El correo debe ser entre 9 y 60 caracteres")
	@Email(message = "Debe ser un correo valido.")
	@Column(nullable = false, unique = true)
	private String email;

	@Min(value = 0)
	 @Max(value = 5000)
	 @Column
	 private Long points;
	 
	@Column(columnDefinition = "ENUM('Admin','Usuario')")
	 public String rol;
	
	@Column
	public String pais;
	
	@Column
	public String user_img;
	
	@Transient
    private MultipartFile imgFile;
	
	@Column( columnDefinition = "TEXT")
	private String descripcion;
	
	@Size(min = 8, message = "Contraseña debe tener más de 8 caracteres.")
	@Column(nullable = false)
	private String password;
	
	@Transient
	@Column
	private String passwordConfirmation;
	
	@Column(columnDefinition = "DATETIME", updatable=false)
    private Date createdAt;
    private Date updatedAt;
    
    @OneToMany(mappedBy="user", fetch = FetchType.LAZY)
    private List<Post> post;
    
    @OneToMany(mappedBy="user", fetch = FetchType.LAZY)
    private List<Comment> comment;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "ratings", 
        joinColumns = @JoinColumn(name = "user_id"), 
        inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private List<Post> rating_post;
    
    @OneToMany(mappedBy="user", fetch = FetchType.LAZY)
    private List<Rating> ratings;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Progress> progress;
    
   @OneToOne(mappedBy="host", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private  Gallery gallery; 
   
   @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(
   name = "messages", 
   joinColumns = @JoinColumn(name = "receptor_id"), 
   inverseJoinColumns = @JoinColumn(name = "emisor_id")
   )
   private List<User> receptor;
   
   @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(
   name = "messages", 
   joinColumns = @JoinColumn(name = "emisor_id"), 
   inverseJoinColumns = @JoinColumn(name = "receptor_id")
   )
   private List<User> emisor;
  
   @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(
       name = "likes", 
       joinColumns = @JoinColumn(name = "user_id"), 
       inverseJoinColumns = @JoinColumn(name = "comment_id")
   )
   private List<Comment> comment_likes;
   
   @OneToMany(mappedBy="user", fetch = FetchType.LAZY)
   private List<Like> likes;
   
   @OneToMany(mappedBy="user", fetch = FetchType.LAZY)
   private List<Notification> notification;

	public User() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Rating> getRatings() {
		return ratings;
	}

	public void setRatings(List<Rating> ratings) {
		this.ratings = ratings;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getPoints() {
		return points;
	}

	public void setPoints(Long points) {
		this.points = points;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	public String getUser_img() {
		return user_img;
	}

	public void setUser_img(String user_img) {
		this.user_img = user_img;
	}

	public MultipartFile getImgFile() {
		return imgFile;
	}

	public void setImgFile(MultipartFile imgFile) {
		this.imgFile = imgFile;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirmation() {
		return passwordConfirmation;
	}

	public void setPasswordConfirmation(String passwordConfirmation) {
		this.passwordConfirmation = passwordConfirmation;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<Post> getPost() {
		return post;
	}

	public void setPost(List<Post> post) {
		this.post = post;
	}

	public List<Comment> getComment() {
		return comment;
	}

	public void setComment(List<Comment> comment) {
		this.comment = comment;
	}

	public List<Post> getRating_post() {
		return rating_post;
	}

	public void setRating_post(List<Post> rating_post) {
		this.rating_post = rating_post;
	}

	public List<Progress> getProgress() {
		return progress;
	}

	public void setProgress(List<Progress> progress) {
		this.progress = progress;
	}

	public Gallery getGallery() {
		return gallery;
	}

	public void setGallery(Gallery gallery) {
		this.gallery = gallery;
	}

	public List<User> getReceptor() {
		return receptor;
	}

	public void setReceptor(List<User> receptor) {
		this.receptor = receptor;
	}

	public List<User> getEmisor() {
		return emisor;
	}

	public void setEmisor(List<User> emisor) {
		this.emisor = emisor;
	}

	public List<Notification> getNotification() {
		return notification;
	}

	public void setNotification(List<Notification> notification) {
		this.notification = notification;
	}

	@PrePersist
    protected void onCreate(){
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = new Date();
    }

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public List<Comment> getComment_likes() {
		return comment_likes;
	}

	public void setComment_likes(List<Comment> comment_likes) {
		this.comment_likes = comment_likes;
	}

	public List<Like> getLikes() {
		return likes;
	}

	public void setLikes(List<Like> likes) {
		this.likes = likes;
	}
	
	
	
}

