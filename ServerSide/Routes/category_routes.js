const express = require("express");
const router = express.Router();

const Category = require("../Controllers/categories");
const authenticate = require("../Common/auth_middleware");

router.get("/:id", authenticate, Category.getPostsByCategoryName);

module.exports = router;
