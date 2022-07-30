const express = require("express");
const router = express.Router();

const Notification = require("../Controllers/notifications");
const authenticate = require("../Common/auth_middleware");

/**
 * @swagger
 * components:
 *   schemas:
 *      Notification:
 *        type: object
 *        required:
 *          - sender
 *          - post
 *          - time
 *          - type
 *        properties:
 *          sender:
 *                type: ObjectId
 *                ref: 'User'
 *                description: The id of user who send the post
 *          post:
 *                type: ObjectId
 *                ref: 'Post'
 *                description: The id of the post
 *          time:
 *                type: Date
 *                description: The post creation date
 *          type:
 *                type: String
 *                description: The notification type --> Like, Comment, New post, New sos call
 *          circle:
 *                type: Number
 *                description: Circle
 *        example:
 *          sender: '62264bf53dfa30aef30f6ca0'
 *          post: '264b62f53d0aef3fa36ca00f'
 *          time: '2022-03-07 18:16:21'
 *          type: 'Comment'
 *          circle: 1
 */

/**
 * @swagger
 * /post:
 *   get:
 *      summary: get all notifications
 *      tags: [Post API]
 *      responses:
 *          200:
 *              description: The notifications list
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/Notification'
 *
 */

router.get("/allNotifications", authenticate, Notification.getAllNotifications);
router.get(
  "/allNotificationsByID/:id",
  authenticate,
  Notification.getAllNotificationsByID
);

/**
 * @swagger
 * /notification/add:
 *   post:
 *      summary: add new notification
 *      tags: [Post API]
 *      requestBody:
 *          required: true
 *          content:
 *              application/json:
 *                  schema:
 *                      $ref: '#/components/schemas/Notification'
 *      responses:
 *          200:
 *              description: The new notification
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/Notification'
 */

router.post("/add", authenticate, Notification.addNotification);
router.post("/sendNotification", authenticate, Notification.sendNotification);
router.get(
  "/numberOfNewNotifications/:id",
  authenticate,
  Notification.numberOfNewNotifications
);
router.get(
  "/updateUserNotifications/:id",
  authenticate,
  Notification.updateUserNotifications
);

module.exports = router;
