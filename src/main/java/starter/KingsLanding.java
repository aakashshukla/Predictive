package starter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import analytics.AnalyticsEngine;
import authentications.UserEngine;
import interfaces.IAnalyticsEngine;
import interfaces.IUserEngine;
import mannyobjects.Address;
import mannyobjects.Location;
import mannyobjects.UserMatrix;
import mannyobjects.UserProfile;

/*
 * This Java source file was auto generated by running 'gradle buildInit --type java-library'
 * by 'chiragparmar' at '24/05/16 10:40 AM' with Gradle 2.9
 *
 * @author chiragparmar, @date 24/05/16 10:40 AM
 */
@Controller
public class KingsLanding implements ErrorController{

	private IUserEngine userEngine = null;
	private IAnalyticsEngine analyticsEngine = null;

	@Autowired(required = true)
	public KingsLanding(AnalyticsEngine analyticsEngine, UserEngine userEngine) {
		super();
		this.userEngine = userEngine;
		this.analyticsEngine = analyticsEngine;
	}
	
	@RequestMapping("/error")
	public String getErrorPage(Model model) {
		return "error";
	}

	@RequestMapping("/home")
	public String getApplicationPage(Model model) {
		return "bootstrap/index";
	}
	
	@RequestMapping("/profile")
	public String getUsreProfile(Model model) {
		return "bootstrap/user";
	}

	@RequestMapping("/")
	public String getLandingPage(Model model) {
		return "landing";
	}
	
	@RequestMapping("/predict")
	public String getPrerict(Model model){
		return "bootstrap/predict";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public @ResponseBody
	String goLogin(WebRequest request) {
		
		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String imageUrl = request.getParameter("imageUrl");
		
		Application.EManager.getTransaction().begin();
		
		UserProfile userProfile = new UserProfile();
		userProfile.setUserName(name);
		userProfile.setUserId(UUID.randomUUID());
		userProfile.setEmail(email);
		userProfile.setImageUrl(imageUrl);
		userProfile.setAddress(new Address());
		userProfile.setUserMatrix(new UserMatrix());
		
		Application.EManager.persist(userProfile);
		Application.EManager.getTransaction().commit();
		
		return "Login Success for " + request.getParameter("name");
	}

	@RequestMapping("/logout")
	public @ResponseBody
	String goLogout(WebRequest request) {
		return "Logout success";
	}
	
	

	/* User Locations Prediction Service */
	@RequestMapping(value = "/predict", method = RequestMethod.POST)
	public @ResponseBody
	Map<String, Object> proivdePredictionAboutUserLocation(WebRequest request) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			String uuidString = request.getParameter("userId");
			UUID uuid = UUID.fromString(uuidString);
			UserProfile userProfile = userEngine.getUser(uuid);
			Location locaiton = analyticsEngine.getCurrentLocation(userProfile);
			data.put("current_location", locaiton);
			data.put("most_recent_location", userProfile.getCurrentLocation());
			data.put("message", userProfile.getMessage());
		} catch (Exception e) {
			data.put("locatoin", "");
			data.put("message",
					"Error while providing user location. Please try later");
		}
		return data;
	}

	/* User Management */
	@RequestMapping(value = "/getuser", method = RequestMethod.POST)
	public @ResponseBody
	Map<String, Object> getUser(WebRequest request) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			data.put("most_recent_location", "");
		} catch (Exception e) {
			data.put("location", "");
			data.put("message",
					"Error while providing user location. Please try later");
		}
		return data;
	}

	@RequestMapping(value = "/createuser", method = RequestMethod.POST)
	public @ResponseBody
	Map<String, Object> createUser(WebRequest request) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {

			String userName = request.getParameter("userName");
			Address address = new Address();
			userEngine.createUser(userName, address);

			data.put("most_recent_location", "");
		} catch (Exception e) {
			data.put("locatoin", "");
			data.put("message",
					"Error while providing user location. Please try later");
		}
		return data;
	}

	@RequestMapping(value = "/updateuser", method = RequestMethod.POST)
	public @ResponseBody
	Map<String, Object> updateUser(WebRequest request) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {

			String userId = request.getParameter("userId");
			UserProfile user = userEngine.getUser(UUID.fromString(userId));
			userEngine.updateUser(user, UUID.fromString(userId));

			data.put("most_recent_location", "");
		} catch (Exception e) {
			data.put("locatoin", "");
			data.put("message",
					"Error while providing user location. Please try later");
		}
		return data;
	}

	@RequestMapping(value = "/deleteuser", method = RequestMethod.POST)
	public @ResponseBody
	Map<String, Object> deleteUser(WebRequest request) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {

			String userId = request.getParameter("userId");
			userEngine.deleteUser(UUID.fromString(userId));

			data.put("most_recent_location", "");
		} catch (Exception e) {
			data.put("locatoin", "");
			data.put("message",
					"Error while providing user location. Please try later");
		}
		return data;
	}
	
	@RequestMapping(value = "/broadcast", method = RequestMethod.POST)
	public @ResponseBody
	String broadcastMyLocation(WebRequest request) {
		
		String lat = request.getParameter("lat");
		String lon = request.getParameter("lon");
		
		//update your location to everyone else
		
		return "Broadcast successful.";
	}

	
	public String getErrorPath() {
		return "/error";
	}

}
