const express = require("express");
const router = express.Router();

const Comment = require("../Controllers/comments");
const authenticate = require("../Common/auth_middleware");

/**
 * @swagger
 * components:
 *   schemas:
 *      Post comment:
 *        type: object
 *        required:
 *          - sender
 *          - post
 *          - message
 *          - time
 *          - isCorrect
 *        properties:
 *          sender:
 *                type: ObjectId
 *                ref: 'User'
 *                description: The ID of user who send the comment
 *          post:
 *                type: ObjectId
 *                ref: 'Post'
 *                description: The post ID to which the comment belongs
 *          message:
 *                type: string
 *                description: The comment text
 *          time:
 *                type: Date
 *                description: Mark if the comment helped the creator of the post
 *          isCorrect:
 *                type: Boolean
 *                description:
 *        example:
 *          sender: '62264bf53dfa30aef30f6ca0'
 *          post: '264b62f53d0aef3fa36ca00f'
 *          message: 'this is swagger test comment message'
 *          time: '2022-03-07 18:16:21'
 *          isCorrect: 'true'
 */

router.get("/:id", authenticate, Comment.getAllComments_Post);

router.post("/add", authenticate, Comment.addComment);
router.post("/edit/:id", authenticate, Comment.editComment);
router.post("/delete/:id", authenticate, Comment.deleteComment);

module.exports = router;
