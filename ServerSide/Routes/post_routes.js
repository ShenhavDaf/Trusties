const express = require('express');
const router = express.Router();

const Post = require('../Controllers/posts');
const authenticate = require('../Common/auth_middleware');

/**
 * @swagger
 * tags:
 *   name: Post API
 *   description: The Post API
 */

/**
 * @swagger
 * components:
 *   schemas:
 *      Post:
 *        type: object
 *        required:
 *          - sender
 *          - title
 *          - message
 *          - time
 *          - role
 *          - status
 *        properties:
 *          sender:
 *                type: ObjectId
 *                ref: 'User'
 *                description: The id of user who send the post
 *          title:
 *                type: string
 *                description: The post title
 *          message:
 *                type: string
 *                description: The post text
 *          time:
 *                type: Date
 *                description: The post creation date
 *          role:
 *                type: String
 *                description: The post type - Standard question or SOS
 *          status:
 *                type: String
 *                description: The post status - open / in process / closed
 *        example:
 *          sender: '62264bf53dfa30aef30f6ca0'
 *          title: 'this is swagger test title'
 *          message: 'this is swagger test message'
 *          time: '2022-03-07 18:16:21'
 *          role: 'SOS'
 *          status: 'OPEN'
 */

/**
 * @swagger
 * /post:
 *   get:
 *      summary: get all posts
 *      tags: [Post Api]
 *      responses:
 *          200:
 *              description: The posts list
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/Post'
 *
 */

router.get('/', authenticate, Post.getPosts);

/**
 * @swagger
 * /post/{id}:
 *   get:
 *      summary: get all posts
 *      tags: [Post Api]
 *      parameters:
 *          - in: path
 *            name: id
 *            schema:
 *              type: string
 *            required: true
 *            description: The post id
 *      responses:
 *          200:
 *              description: The posts list
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/Post'
 *
 */

router.get('/:id', authenticate, Post.getPostsById);

/**
 * @swagger
 * /post:
 *   post:
 *      summary: add new post
 *      tags: [Post API]
 *      requestBody:
 *          required: true
 *          content:
 *              application/json:
 *                  schema:
 *                      $ref: '#/components/schemas/Post'
 *      responses:
 *          200:
 *              description: The new post
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: '#/components/schemas/Post'
 *
 */

router.post('/', authenticate, Post.addPosts);
router.post('/MyPost', authenticate, Post.getMyPosts);

module.exports = router;
