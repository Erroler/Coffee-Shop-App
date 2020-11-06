const { v4: uuidv4 } = require('uuid');
const _ = require('lodash');

module.exports = {
  up: async (queryInterface, Sequelize) => {
    const orders = await queryInterface.bulkInsert('Orders',
      [
        {
          order_id: uuidv4(),
          userId: '95850c47-bfa2-4254-84a8-36b587dfeb27',
          createdAt: new Date(),
          updatedAt: new Date(),
          completed: false,
        },
        {
          order_id: uuidv4(),
          userId: '95850c47-bfa2-4254-84a8-36b587dfeb27',
          createdAt: new Date(),
          updatedAt: new Date(),
          completed: true,
        },
        {
          order_id: uuidv4(),
          userId: '95850c47-bfa2-4254-84a8-36b587dfeb27',
          createdAt: new Date(),
          updatedAt: new Date(),
          completed: true,
        },
        {
          order_id: uuidv4(),
          userId: '95850c47-bfa2-4254-84a8-36b587dfeb27',
          createdAt: new Date(),
          updatedAt: new Date(),
          completed: false,
        },
      ],
      {
        returning: true
      });
    orders.forEach(async (order) => {
      const numberOfItems = _.random(1, 6);
      const items = _.range(1, numberOfItems).map((itemIndex) => {
        return {
          order_item_id: uuidv4(),
          order_id: order.order_id,
          item_id: itemIndex,
          quantity: _.random(3),
          price: _.random(1, 10, true)
        };
      });
      await queryInterface.bulkInsert('OrderItems', items);
    });
  },

  down: async (queryInterface, Sequelize) => {
    queryInterface.bulkDelete('Order', null, {});
    queryInterface.bulkDelete('OrderItem', null, {});
  },
};
