const express = require("express");
const router = express.Router();

const Comment = require('../Controllers/comments');
const authenticate = require('../Common/auth_middleware');

router.post('/:id', authenticate, Comment.addComment);

router.get('/:id', authenticate, Comment.getAllComments_Post);



module.exports = router