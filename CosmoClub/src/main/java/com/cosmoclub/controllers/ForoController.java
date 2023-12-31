package com.cosmoclub.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cosmoclub.components.CalcularTiempoTranscurrido;
import com.cosmoclub.components.SaludoHorario;
import com.cosmoclub.logicaNegocio.Foro;
import com.cosmoclub.models.Comment;
import com.cosmoclub.models.Like;
import com.cosmoclub.models.Like.LikeType;
import com.cosmoclub.models.Post;
import com.cosmoclub.models.Rating;
import com.cosmoclub.models.User;
import com.cosmoclub.services.CommentService;
import com.cosmoclub.services.LikeService;
import com.cosmoclub.services.PostService;
import com.cosmoclub.services.RatingService;
import com.cosmoclub.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ForoController {
	@Autowired
	private UserService userService;
	
	
	@Autowired
	private PostService postService;
	
	
	@Autowired
	private CalcularTiempoTranscurrido calcTiempoTranscurrido;
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private Foro foro;
	
	@Autowired
	private RatingService ratingService;
	
	@Autowired
	private SaludoHorario saludoHorario;

	@Autowired
	private LikeService likeService;
	
	
	@GetMapping("/foro")
	public String foro(@ModelAttribute("newPost") Post newPost, HttpSession session, Model model) {
	    Long userId = (Long) session.getAttribute("userId");
	    if (userId != null) {
	        User user = userService.findUserById(userId);
	        model.addAttribute("user", user);
	        List<Post> allPosts = postService.findAllPosts();
	        model.addAttribute("allPosts", allPosts);

	        // Obtener las calificaciones del usuario para cada post
	        Map<Long, Double> userRatings = ratingService.getUserRatingsForPosts(user);
	        model.addAttribute("userRatings", userRatings);

	        // Calcular el promedio de calificaciones para cada post
	        Map<Long, Double> postRatings = new HashMap<>();
	        for (Post post : allPosts) {
	            Double averageRating = postService.calculateAverageRating(post.getId()); // Usar el ID del post actual
	            postRatings.put(post.getId(), averageRating);
	        }
	        model.addAttribute("postRatings", postRatings);

	        // Crear un mapa para almacenar los recuentos de comentarios por postId
	        Map<Long, Long> commentCounts = new HashMap<>();
	        for (Post post : allPosts) {
	            Long postId = post.getId();
	            Long numberCommentsDash = commentService.countCommentsByPostId(postId);
	            commentCounts.put(postId, numberCommentsDash);
	        }
	        model.addAttribute("commentCounts", commentCounts);

	        // Calcular la diferencia de tiempo y formatearla para cada post
	        for (Post post : allPosts) {
	            String timeAgo = calcTiempoTranscurrido.calcularFecha(post.getCreatedAt());
	            post.setTimeAgo(timeAgo);
	        }
	        
	        String saludar = saludoHorario.obtenerSaludo();
	        model.addAttribute("saludar", saludar);

	        return "views/foro.jsp";
	    } else {
	        return "redirect:/";
	    }
	}

	@PostMapping("/crear-post")
	public String crearPost(@Valid @ModelAttribute("newPost") Post newPost, @RequestParam("post_img") MultipartFile post_img, BindingResult result, Model model, HttpSession session) {
		if (result.hasErrors()) {
			Long userId = (Long) session.getAttribute("userId");
	        User user = userService.findUserById(userId);
	        model.addAttribute("user", user);
	        
	        List<Post> allPosts = postService.findAllPosts();
	        model.addAttribute("allPosts", allPosts);
			return "views/foro.jsp";
		}
		
		Post post = postService.createPost(newPost);
		foro.guardarImgPost(post, post_img);
		
		return "redirect:/foro";
	}
	
	@GetMapping("/post/{id}")
	public String verPost(@PathVariable("id") Long postId, @ModelAttribute("newComment") Comment newComment, HttpSession session, Model model) {
	
	    Long userId = (Long) session.getAttribute("userId");
	    if (userId != null) {
	        User user = userService.findUserById(userId);
	        Post post = postService.findPost(postId);
	        List<Comment> allCommentsPost = commentService.commentsByPost(postId);
			Long numberCommentsPost = commentService.countCommentsByPostId(postId);

	        // Calcular la diferencia de tiempo y formatearla para cada post
	        String timeAgoPost = calcTiempoTranscurrido.calcularFecha(post.getCreatedAt());
	        post.setTimeAgo(timeAgoPost);

	        Map<Long, Long> likesMap = new HashMap<>();
	        Map<Long, Long> dislikesMap = new HashMap<>();

	       
	        for (Comment comment : allCommentsPost) {
	            String timeAgoComment = calcTiempoTranscurrido.calcularFecha(comment.getCreatedAt());
	            comment.setTimeAgo(timeAgoComment);

	            // Contar la cantidad de "me gusta" y "no me gusta" para cada comentario
	            // Contar la cantidad de "me gusta" para este comentario
	            Long likesCount = likeService.countLikesByCommentAndType(comment, LikeType.ME_GUSTA);
	            likesMap.put(comment.getId(), likesCount);

	            // Contar la cantidad de "no me gusta" para este comentario
	            Long dislikesCount = likeService.countLikesByCommentAndType(comment, LikeType.NO_ME_GUSTA);
	            dislikesMap.put(comment.getId(), dislikesCount);
	        }

	        Double userRating = ratingService.getUserRatingForPost(user, post);
	        model.addAttribute("userRating", userRating);

	        model.addAttribute("user", user);
	        model.addAttribute("post", post);
	        model.addAttribute("allCommentsPost", allCommentsPost);
	        model.addAttribute("numberCommentsPost", numberCommentsPost);
	        model.addAttribute("likesMap", likesMap);
	        model.addAttribute("dislikesMap", dislikesMap);

	        return "views/post.jsp";
	    } else {
	        return "redirect:/";
	    }
	}
	
	@PostMapping("/post/{postId}/comment")
	public String comentarPost(@PathVariable("postId") Long postId, @Valid @ModelAttribute("newComment") Comment newComment, BindingResult result, HttpSession session, Model model) {
		if (result.hasErrors()) {
			Long userId = (Long) session.getAttribute("userId");
			User user = userService.findUserById(userId);
			Post post = postService.findPost(postId);
			List<Comment> allCommentsPost = commentService.commentsByPost(postId);
			Long numberCommentsPost = commentService.countCommentsByPostId(postId); 
			model.addAttribute("user", user);
			model.addAttribute("post", post);
			model.addAttribute("allCommentsPost", allCommentsPost);
			model.addAttribute("numberCommentsPost", numberCommentsPost); 
			return "views/post.jsp";
		}
		Long userId = (Long) session.getAttribute("userId");
	    User user = userService.findUserById(userId);
	    Post post = postService.findPost(postId);
	    
	    List<Comment> comments = post.getComments();
	    newComment.setUser(user);
	    newComment.setPost(post);
	    comments.add(newComment);
	    post.setComments(comments);
	    
	    commentService.createComment(newComment);
	    postService.createPost(post);
		return "redirect:/post/" + post.getId();
		
	}
	
	@PostMapping("/post/{postId}/rating")
	public String calificarPost(@PathVariable("postId") Long postId,@RequestParam("rating") Double rating,HttpSession session, RedirectAttributes redirectAttributes) {

	    Long userId = (Long) session.getAttribute("userId");

	    if (userId != null) {
	        User user = userService.findUserById(userId);
	        Post post = postService.findPost(postId);

	        if (user != null && post != null) {
	            // Verifica si el usuario ya calificó este post
	        	boolean userHasRated = post.getRating_user().stream().anyMatch(ratingUser -> ratingUser.getId().equals(userId));

	            if (!userHasRated) {
	                Rating newRating = new Rating();
	                newRating.setUser(user);
	                newRating.setPost(post);
	                newRating.setRating(rating);

	                ratingService.save(newRating);

	                redirectAttributes.addFlashAttribute("successMessage", "¡Calificación agregada con éxito!");
	            } else {
	                redirectAttributes.addFlashAttribute("errorMessage", "Ya has calificado este post.");
	            }
	        } else {
	            redirectAttributes.addFlashAttribute("errorMessage", "Usuario o post no encontrado.");
	        }
	    } else {
	        redirectAttributes.addFlashAttribute("errorMessage", "Usuario no identificado.");
	    }

	    return "redirect:/post/" + postId;
	}
	
	@PostMapping("/{postId}/comments/{commentId}/{likeOrDislike}")
	public ResponseEntity<Map<String, Object>> likeOrDislikeComment(
	    @PathVariable("postId") Long postId,
	    @PathVariable("commentId") Long commentId,
	    @PathVariable("likeOrDislike") String likeOrDislike,
	    HttpSession session
	) {
	    Long userId = (Long) session.getAttribute("userId");

	    if (userId != null) {
	        User user = userService.findUserById(userId);
	        Post post = postService.findPost(postId);
	        Comment comment = commentService.findcommentById(commentId);

	        if (user != null && post != null && comment != null) {
	            // Buscar un registro existente de Like para este usuario y comentario
	            Like existingLike = likeService.findByUserAndComment(user, comment);

	            if ("like".equals(likeOrDislike)) {
	                if (existingLike != null && existingLike.getTipo() == LikeType.ME_GUSTA) {
	                    // El usuario ya dio "Me gusta" previamente al mismo comentario, elimina el registro
	                    likeService.deleteLike(existingLike);
	                } else {
	                    // Eliminar cualquier voto anterior (ya sea "Me gusta" o "No me gusta")
	                    likeService.deletePreviousVote(user, comment);

	                    // Crear un nuevo registro de "Me gusta"
	                    Like like = new Like();
	                    like.setTipo(LikeType.ME_GUSTA);
	                    like.setComentario(comment);
	                    like.setUser(user);
	                    likeService.saveLike(like);
	                }
	            } else if ("dislike".equals(likeOrDislike)) {
	                if (existingLike != null && existingLike.getTipo() == LikeType.NO_ME_GUSTA) {
	                    // El usuario ya dio "No me gusta" previamente al mismo comentario, elimina el registro
	                    likeService.deleteLike(existingLike);
	                } else {
	                    // Eliminar cualquier voto anterior (ya sea "Me gusta" o "No me gusta")
	                    likeService.deletePreviousVote(user, comment);

	                    // Crear un nuevo registro de "No me gusta"
	                    Like dislike = new Like();
	                    dislike.setTipo(LikeType.NO_ME_GUSTA);
	                    dislike.setComentario(comment);
	                    dislike.setUser(user);
	                    likeService.saveLike(dislike);
	                }
	            }

	            // Obtener los nuevos contadores después de realizar el voto
	            Long likesCount = likeService.countLikesByCommentAndType(comment, LikeType.ME_GUSTA);
	            Long dislikesCount = likeService.countLikesByCommentAndType(comment, LikeType.NO_ME_GUSTA);

	            // Crear un mapa para la respuesta JSON
	            Map<String, Object> response = new HashMap<>();
	            response.put("message", "Me gusta registrado correctamente");
	            response.put("likesCount", likesCount);
	            response.put("dislikesCount", dislikesCount);

	            // Devolver la respuesta JSON
	            return ResponseEntity.ok(response);
	        }
	    }

	    // Manejar el caso en el que no se encuentre el usuario, el post o el comentario
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("message", "No se pudo registrar el me gusta"));
	}
		 
}

