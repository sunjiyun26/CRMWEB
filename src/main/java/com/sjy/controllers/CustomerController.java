package com.sjy.controllers;

import ch.qos.logback.classic.Logger;
import com.sjy.controllers.util.CustomerToExcel;
import com.sjy.controllers.util.GetCurrentInfo;
import com.sjy.entities.Customer;
import com.sjy.service.CustomerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
/**
 * 客户控制类
 * @author Administrator
 *
 */
@Controller
public class CustomerController {
	
	private static final Logger LOGGER=(Logger) LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	private CustomerService customerService;
   
	/**
	 * 自动注入当前登录用户信息组件
	 */
	@Autowired
    private GetCurrentInfo currentInfo;
	
	@RequestMapping(value="/customer-add",method=RequestMethod.GET)
	public String add(){
		return "/customer/add";
	}
	
	@RequestMapping(value="/customer-add",method=RequestMethod.POST)
	@ResponseBody
	public String add(Customer customer){
		UserDetails currentUser=this.currentInfo.getCurrentInfo();//获取当前登录用户
       LOGGER.warn("{}增加了一个客户：{}",currentUser.getUsername(),customer.getCustomerName());
		this.customerService.add(customer);
		return "{result:true,message:\"保存成功\"}";
	}
	
	@RequestMapping(value="/customer-modify/{csId}",method=RequestMethod.GET)
	public String modify(@PathVariable int csId,ModelMap map){
	    Customer currentCustomer=this.customerService.get(csId);		
		map.addAttribute("currentCustomer", currentCustomer);		
		return "/customer/modify";
	}
	
	@RequestMapping(value="/customer-modify",method=RequestMethod.POST)
	@ResponseBody
	public String modify(Customer customer){
		UserDetails currentUser=this.currentInfo.getCurrentInfo();
		LOGGER.warn("{}修改了客户{}的信息",currentUser.getUsername(),customer.getCustomerName());
	  this.customerService.modify(customer);
	  return "{result:true,message:\"修改成功\"}";		
	}
	
	@RequestMapping("/search_customer")
	public String search(String searchContent,ModelMap map){
		List<Customer> allCustomer=this.customerService.search(searchContent);
		map.addAttribute("list", allCustomer);		
		return "/customer/allCustomer";
	}
	
	@RequestMapping("/allCustomerWithPage/{page}")
	public String getAllWithPage(@PathVariable("page")Integer page,ModelMap map){
		Page<Customer> customerPage=this.customerService.getAllWithPage(page, 5);//约定分页中每页显示五条信息
		int pageSum=customerPage.getTotalPages();//获取总共页
		List<Customer> list=customerPage.getContent();//将springdata中分页查询中方法查询的内容以list方式保存
		map.addAttribute("list", list);
		map.addAttribute("pageSum", pageSum);
		map.addAttribute("page", page);
		return "/customer/allCustomer";
	}
	
	
	@RequestMapping("/toExcel")
	@ResponseBody
	public String toExcel(){
		List<Customer> list =this.customerService.getAll();
		CustomerToExcel.toExcel(list);
		return "导出成功";
	}
	
}
