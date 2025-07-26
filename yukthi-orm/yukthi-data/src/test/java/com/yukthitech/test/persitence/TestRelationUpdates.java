package com.yukthitech.test.persitence;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.persistence.GenericRepository;
import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.test.persitence.entity.Customer;
import com.yukthitech.test.persitence.entity.CustomerAddress;
import com.yukthitech.test.persitence.entity.CustomerGroup;
import com.yukthitech.test.persitence.entity.ICustomerGroupRepository;
import com.yukthitech.test.persitence.entity.ICustomerRepository;
import com.yukthitech.test.persitence.entity.IOrderItemRepository;
import com.yukthitech.test.persitence.entity.IOrderRepository;
import com.yukthitech.test.persitence.entity.Order;
import com.yukthitech.test.persitence.entity.OrderItem;

public class TestRelationUpdates extends TestSuiteBase 
{
	private static Logger logger = LogManager.getLogger(TestRelationUpdates.class);
	
    private ICustomerRepository customerRepository;
    private ICustomerGroupRepository customerGroupRepository;
    private IOrderRepository orderRepository;
    private IOrderItemRepository orderItemRepository;
    
	@Override
	protected void initFactoryBeforeClass(RepositoryFactory factory)
    {
        GenericRepository genericRepository = new GenericRepository(factory);
		factory.dropRepository(OrderItem.class);
		factory.dropRepository(Order.class);
		factory.dropRepository(Customer.class);
		factory.dropRepository(CustomerAddress.class);
		factory.dropRepository(CustomerGroup.class);
		
		// Add some groups so that groups and customers will not have same ids
		for(int i = 0; i < 10; i++)
		{
			genericRepository.save(new CustomerGroup("TG-" + i, null));
		}

		CustomerGroup group1 = new CustomerGroup("Group1", null);
		CustomerGroup group2 = new CustomerGroup("Group2", null);
		CustomerGroup group3 = new CustomerGroup("Group3", null);
		CustomerGroup group4 = new CustomerGroup("Group4", null);

		genericRepository.save(group1);
		genericRepository.save(group2);
		genericRepository.save(group3);
		genericRepository.save(group4);

		Customer customer1 = new Customer("Customer1", Arrays.asList(group1, group2), null);
		Customer customer2 = new Customer("Customer2", Arrays.asList(group1, group3), null);
		Customer customer3 = new Customer("Customer3", Arrays.asList(group3), null);
		Customer customer4 = new Customer("Customer4", Arrays.asList(group1), null);

		genericRepository.save(customer2);
		genericRepository.save(customer4);
		genericRepository.save(customer1);
		genericRepository.save(customer3);

		Order order1 = new Order("Order1", 1, customer1, 100);
		Order order2 = new Order("Order2", 1, customer1, 100);
		Order order3 = new Order("Order3", 1, customer2, 100);
		Order order4 = new Order("Order4", 1, customer2, 100);

		genericRepository.save(order1);
		genericRepository.save(order2);
		genericRepository.save(order3);
		genericRepository.save(order4);
		
		customerRepository = factory.getRepository(ICustomerRepository.class);
        customerGroupRepository = factory.getRepository(ICustomerGroupRepository.class);
        orderRepository = factory.getRepository(IOrderRepository.class);
        orderItemRepository = factory.getRepository(IOrderItemRepository.class);
    }

    /**
     * Ensure forward many-to-many relation is working properly.
     */
    @Test
    public void testRelationUpdates()
    {
        int count = customerRepository.updateCustomerGroups("Customer1", 
        		Arrays.asList(customerGroupRepository.findByName("Group2"), customerGroupRepository.findByName("Group3")));
        Customer customer1 = customerRepository.findByName("Customer1");
        Assert.assertEquals(customer1.getCustomerGroups().size(), 2);
        
        Set<String> groupNames = customer1.getCustomerGroups()
        		.stream()
        		.map(grp -> grp.getName())
        		.collect(Collectors.toSet());
        
        logger.debug("Got matching groups as: {} [Upd Count: {}]", groupNames, count);
        
        Assert.assertTrue(groupNames.contains("Group2"));
        Assert.assertTrue(groupNames.contains("Group3"));
        
        Assert.assertTrue(count > 1);
    }

    /**
     * Ensure reverse many-to-many relation is working properly.
     */
    @Test(dependsOnMethods = "testRelationUpdates")
    public void testReverseRelationUpdates()
    {
    	int count = customerGroupRepository.updateCustomers("Group3", 
    			Arrays.asList(
    				customerRepository.findByName("Customer2"), 
    				customerRepository.findByName("Customer4")
    				));
    	
    	CustomerGroup cg3 = customerGroupRepository.findByName("Group3");
        
        Assert.assertEquals(cg3.getCustomers().size(), 2);

        Set<String> custNames = cg3.getCustomers()
        		.stream()
        		.map(cust -> cust.getName())
        		.collect(Collectors.toSet());
        
        logger.debug("Got matching customers as: {}", custNames);
        
        // Customer3 should get removed
        Assert.assertTrue(custNames.contains("Customer4")); // new customer
        Assert.assertTrue(custNames.contains("Customer2")); // unchanged one
        
        Assert.assertTrue(count > 1);
    }

    /**
     * Ensure one-to-many relation is working properly.
     */
    @Test(dependsOnMethods = "testReverseRelationUpdates")
    public void testMapRelationUpdates()
    {
    	int count = customerRepository.updateOrders("Customer1", 
        		Arrays.asList(orderRepository.findOrderByTitle("Order3"), orderRepository.findOrderByTitle("Order1")));
        Customer customer1 = customerRepository.findByName("Customer1");
        
        Assert.assertEquals(customer1.getOrders().size(), 2);
        
        Set<String> orderNames = customer1.getOrders()
        		.stream()
        		.map(ord -> ord.getTitle())
        		.collect(Collectors.toSet());
        
        logger.debug("Got matching orders as: {}", orderNames);
        
        // Order2 gets removed
        Assert.assertTrue(orderNames.contains("Order1")); // this is unchanged
        Assert.assertTrue(orderNames.contains("Order3")); // new addition

        Assert.assertTrue(count > 1);
    }

    /**
     * Ensure when non-existing entity is provided, it is handled properly.
     */
    @Test(dependsOnMethods = "testMapRelationUpdates")
    public void testWithInvalidEntity()
    {
    	int count = customerRepository.updateOrders("Customer3", 
        		Arrays.asList(new Order(10000)));
    	
    	Assert.assertEquals(count, 0);

    	Customer customer3 = customerRepository.findByName("Customer3");
        Assert.assertEquals(customer3.getOrders().size(), 0);
    }

    /**
     * Check update-cascade functionality.
     */
    @Test//(dependsOnMethods = "testWithInvalidEntity")
    public void testUpdateCascade()
    {
    	Order order4 = orderRepository.findOrderByTitle("Order4");
    	int count = orderRepository.updateItems("Order4", 151, Arrays.asList(
    			new OrderItem("item1", 10, null),
    			new OrderItem("item2", 10, null),
    			new OrderItem("item3", 30, null)
    		));
    	
    	Assert.assertTrue(count > 1);
    	
    	// Ensure order field is updated
    	order4 = orderRepository.findOrderByTitle("Order4");
    	Assert.assertEquals(order4.getOrderNo(), 151);
    	
    	// Ensure items are added properly
    	List<OrderItem> items = orderItemRepository.fetchItems(order4.getId());
    	Set<String> names = items
    			.stream()
    			.map(item -> item.getItemName())
    			.collect(Collectors.toSet());
    	Assert.assertEquals(names, Set.of("item1", "item2", "item3"));

    	OrderItem item1 = items.stream()
    			.filter(item -> item.getItemName().equals("item1"))
    			.findFirst()
    			.get();

    	// Add a new item, remove existing item, update existing item, keep one item unchanged
    	item1.setQuantity(110);
    	
    	count = orderRepository.updateItems("Order4", 151, Arrays.asList(
    			new OrderItem("item4", 40, null),     // new item
    			//new OrderItem("item2", 10, null),   // item to remove
    			new OrderItem("item3", 30, null),    // item unchanged
    			item1                                // modified item
    		));
    	
    	Assert.assertTrue(count > 0);

    	// ensure changes are done properly
    	items = orderItemRepository.fetchItems(order4.getId());
    	names = items
    			.stream()
    			.map(item -> item.getItemName())
    			.collect(Collectors.toSet());
    	Assert.assertEquals(names, Set.of("item1", "item3", "item4"));

    	item1 = items.stream()
    			.filter(item -> item.getItemName().equals("item1"))
    			.findFirst()
    			.get();

    	Assert.assertEquals(item1.getQuantity(), 110);
    }

    @Test
    public void testUpdateCascade_withJoin()
    {
    	CustomerGroup cust4 = customerGroupRepository.findByName("Group4");
    	int count = customerGroupRepository.updateFullCustomers("Group4", Arrays.asList(
    			new Customer("JCust1", null, null),
    			new Customer("JCust2", null, null),
    			new Customer("JCust3", null, null)
    		));
    	
    	// Ensure order field is updated
    	cust4 = customerGroupRepository.findByName("Group4");
    	
    	// Ensure items are added properly
    	List<Customer> customers = cust4.getCustomers();
    	Set<String> names = customers
    			.stream()
    			.map(item -> item.getName())
    			.collect(Collectors.toSet());
    	Assert.assertEquals(names, Set.of("JCust1", "JCust2", "JCust3"));

    	Customer cust1 = customers.stream()
    			.filter(item -> item.getName().equals("JCust1"))
    			.findFirst()
    			.get();

    	// Add a new item, remove existing item, update existing item, keep one item unchanged
    	cust1.setName("JCust101");
    	
    	count = customerGroupRepository.updateFullCustomers("Group4", Arrays.asList(
    			new Customer("JCust4", null, null),     // new item
    			//new Customer("JCust2", null, null),   // item to remove
    			new Customer("JCust3", null, null),    // item unchanged
    			cust1                                // modified item
    		));
    	
    	Assert.assertTrue(count > 0);

    	// ensure changes are done properly
    	cust4 = customerGroupRepository.findByName("Group4");
    	customers = cust4.getCustomers();
    	names = customers
    			.stream()
    			.map(item -> item.getName())
    			.collect(Collectors.toSet());
    	Assert.assertEquals(names, Set.of("JCust101", "JCust3", "JCust4"));
    }
}
