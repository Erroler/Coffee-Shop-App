const { Op, Sequelize } = require('sequelize');
const { v4: uuidv4 } = require('uuid');
const db = require('../db');
const { Order, OrderItem } = require('../models');
const voucherService = require('./voucherService');
const userService = require('./userService');
const menuService = require('./menuService');
const utils = require('../utils/utils');

/**
 * Returns all orders of a given user.
 * @param {uuid} userId the user's UUID
 */
async function getOrders(userId) {
  const orders = await Order.findAll({
    where: {
      [Op.and]: [
        { userId },
      ],
    },
  });

  return orders;
}

/**
 * Returns the order matching a given order_id from a given user.
 * @param {uuid} order_id the order's id
 * @param {uuid} user_id the user's id
 */
async function getOrder(order_id, user_id) {
  const order = await Order.findOne({
    where: {
      [Op.and]: [
        { userId: user_id },
        { order_id },
      ],
    },
  });

  return order;
}

/**
 * Returns all orders items associated with the orders IDs.
 * @param {orderIDs} orderIDs the IDs of the orders as array
 */
async function getOrderItems(orderIDs) {
  const orders = await OrderItem.findAll({
    where: {
      order_id: {
        [Op.or]: orderIDs,
      },
    },
  });

  return orders;
}

async function createOrder(orderId, userId, orderItems, orderItemsQuantities, vouchers, total) {
  const result = await db.transaction(async (t) => {
    const order = await Order.create({
      order_id: orderId,
      userId,
      total,
      completed: true,
    }, { transaction: t });

    for (let i = 0; i < orderItems.length; i += 1) {
      const item = orderItems[i];
      const quantity = orderItemsQuantities[i];

      await OrderItem.create({
        order_item_id: uuidv4(),
        item_id: item.id,
        order_id: orderId,
        price: item.price,
        quantity,
      }, { transaction: t });
    }

    for (const voucher of vouchers) {
      await voucherService.useVoucher(voucher.voucherId, orderId, t);
    }

    const payedCoffees = utils.countPayedCoffees(orderItems, orderItemsQuantities, vouchers);

    const earnedVouchers = await voucherService.emitVouchers(
      userId, orderId, payedCoffees, total, t,
    );

    await userService.updateUserTotals(userId, payedCoffees, total);

    return { order, earnedVouchers };
  });

  let completeOrderItems = await result.order.getOrderItems();
  completeOrderItems = completeOrderItems.map((orderItem) => orderItem.dataValues);

  const itemsWithInfo = [];

  for (const orderItem of completeOrderItems) {
    const menuItem = await menuService.getMenuItemById(orderItem.item_id);
    itemsWithInfo.push({ orderItem, menuItem: menuItem.dataValues });
  }

  const user = await userService.getUser({ uuid: userId });
  const orderWithItems = {
    ...result.order.dataValues,
    orderItems: itemsWithInfo,
    user: user.name,
    vouchers,
    earnedVouchers: result.earnedVouchers,
  };

  return orderWithItems;
}

module.exports = {
  getOrders,
  getOrderItems,
  getOrder,
  createOrder,
};