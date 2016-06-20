package at.fh.swenga.ima.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import at.fh.swenga.ima.dao.TaskRepository;
import at.fh.swenga.ima.model.TaskModel;

@Controller
public class TaskController {
	@Autowired
	TaskRepository taskRepository;

	@RequestMapping(value = { "/calendarJson", "json" }, produces = "application/json")
	public @ResponseBody String getCalendarJson(HttpServletResponse response, @AuthenticationPrincipal UserDetails userDetails) {		
        // Convert model to JSON string
        response.setCharacterEncoding("UTF-8");        
        String json = new Gson().toJson(taskRepository.findByUserName(userDetails.getUsername()));
        return json;
	}
	
	

	@RequestMapping(value = { "/findTask" })
	public String find(Model model, @RequestParam String searchString, @ModelAttribute("type") String type) {
		// @RequestParam => take it
		// @ModelAttribute => take it and put it back into the model!!
		List<TaskModel> tasks = null;
		int count = 0;

		switch (type) {
		case "findAll":
			tasks = taskRepository.findAll();
			break;
		case "findByTaskName":
			tasks = taskRepository.findByTaskName(searchString);
			break;
		case "findByDescription":
			tasks = taskRepository.findByDescription(searchString);
			break;
		case "findByStatus":
			tasks = taskRepository.findByStatus(Boolean.parseBoolean(searchString));
			break;
						
		default:
			tasks = taskRepository.findAll();
		}

		model.addAttribute("tasks", tasks);
		model.addAttribute("count", count);
		return "taskIndex";
	}

	@RequestMapping("/fillTasks")
	@Transactional
	public String fillData(Model model, @AuthenticationPrincipal UserDetails userDetails) {

		/*
		// Creates always the same data
		DataFactory df = new DataFactory();
		Date now = new Date();

		for (int i = 0; i < 10; i++) {
			TaskModel tm = new TaskModel(df.getFirstName(), df.getFirstName(), df.chance(50), df.getDateBetween(now,df.getDate(2017, 1, 1) ));
			taskRepository.save(tm);
		}
		 */
        
        List<TaskModel> tasks = new ArrayList<TaskModel>();
        tasks.add(new TaskModel(1, "Task1 test", "blabla", false, new Date(), new Date(), "Graz", userDetails.getUsername()));
        tasks.add(new TaskModel(2, "Task2 hello world", "blabla", false, new Date(), new Date(), "Graz", userDetails.getUsername()));
        tasks.add(new TaskModel(3, "Another test", "blabla", false, new Date(), new Date(), "Graz", userDetails.getUsername()));
        
        taskRepository.save(tasks);

		return "forward:/calendar";
	}


	@RequestMapping(value = { "/calendar", "list" })
	public String showCalendar(Model model) {
		model.addAttribute("pageTitle", "Calendar View");
        return "calendarIndex";
	}
	

	@RequestMapping(value = "/addTask", method = RequestMethod.GET)
	public String showAddTaskkForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		model.addAttribute("pageTitle", "Add Task");
		model.addAttribute("userDetails", userDetails);
		return "taskEdit";
	}
	
	@RequestMapping(value = "/addTask", method = RequestMethod.POST)
	public String addTask(@Valid @ModelAttribute TaskModel newTaskModel, BindingResult bindingResult,
			Model model, @AuthenticationPrincipal UserDetails userDetails) {
 
		if (bindingResult.hasErrors()) {
			String errorMessage = "";
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				errorMessage += fieldError.getField() + " is invalid<br>";
			}
			// put the errors into the model
			model.addAttribute("errorMessage", errorMessage);
			return "forward:/calendar";
		}
 
		TaskModel task = taskRepository.findTaskById(newTaskModel.getId());
		
		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
		if (task != null) {
			model.addAttribute("errorMessage", "Task already exists!<br>");
		} else if (!authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")) && !newTaskModel.getUserName().equals(userDetails.getUsername())) {
			// not an admin and not the current user
			model.addAttribute("errorMessage", "Not authorized to add task for " + newTaskModel.getUserName());
		} else {
			TaskModel savedTaskModel = taskRepository.save(newTaskModel);
			savedTaskModel.setUrl("#"); // setter automatically generates a url (with id) to edit this task
			taskRepository.save(savedTaskModel);
			model.addAttribute("message", "Added new task" + newTaskModel.getTitle());
		}
 
		return "forward:/calendar";
	}
	

	@RequestMapping(value = { "/editTask", "edit" })
	public String editTask(Model model, @RequestParam int id, @AuthenticationPrincipal UserDetails userDetails) {
		TaskModel task = taskRepository.findTaskById(id);

		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
		
		if (task == null) {
			model.addAttribute("errorMessage", "Couldn't find task with id " + id);
			return "forward:/calendar";
		} else if (!authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")) && !task.getUserName().equals(userDetails.getUsername())) {
			// not an admin and not the current user
			model.addAttribute("errorMessage", "Not authorized view tasks of " + task.getUserName());
			return "forward:/calendar";
		} else {
			model.addAttribute("task", task);
			model.addAttribute("pageTitle", "Edit Task");
			return "taskEdit";
		}
	}

	
	@RequestMapping(value = "/editTask", method = RequestMethod.POST)
	public String editStudent(@Valid @ModelAttribute TaskModel editedTaskModel, BindingResult bindingResult,
			Model model, @AuthenticationPrincipal UserDetails userDetails) {
 
		if (bindingResult.hasErrors()) {
			String errorMessage = "";
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				errorMessage += fieldError.getField() + " is invalid<br>";
			}
			model.addAttribute("errorMessage", errorMessage);
			return "forward:/calendar";
		}
 
		TaskModel task = taskRepository.findTaskById(editedTaskModel.getId());
		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
		
		if (task == null) {
			model.addAttribute("errorMessage", "Task" + editedTaskModel.getId() + "does not exist!<br>");
		} else if (!authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")) && !task.getUserName().equals(userDetails.getUsername())) {
			// not an admin and not the current user
			model.addAttribute("errorMessage", "Not authorized edit tasks of " + task.getUserName());
		}  else if (!authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")) && !editedTaskModel.getUserName().equals(userDetails.getUsername())) {
			// changed owner to different user
			model.addAttribute("errorMessage", "Not authorized to change owner to " + editedTaskModel.getUserName());
		} else {

			task.setTitle(editedTaskModel.getTitle());
			task.setDescription(editedTaskModel.getDescription());
			task.setStatus(editedTaskModel.getStatus());
			task.setStart(editedTaskModel.getStart());
			task.setEnd(editedTaskModel.getEnd());
			task.setPlace(editedTaskModel.getPlace());
			task.setUserName(editedTaskModel.getUserName());
			task.setUrl("#"); // setter automatically generates a url to edit this task
			
			model.addAttribute("message", "Changed task " + editedTaskModel.getId());
			taskRepository.save(task);
		}
 
		return "forward:/calendar";
	}
	

}
