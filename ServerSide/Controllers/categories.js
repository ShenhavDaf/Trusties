const Post = require("../Models/post_model");
const User = require("../Models/user_model");
const Sos = require("../Models/sos_model");
const Comment = require("../Models/comment_model");
const Category = require("../Models/category_model");

const { route } = require("../Routes/category_routes");
const router = require("../Routes/category_routes");

Category.countDocuments({}, function (err, count) {
  console.log("Number of users:", count);

  // decide ur other logic

  // if count is 0 or less
  if (count <= 0) {
    var categoryCar = {
      name: "Car",
    };
    var categoryHouse = {
      name: "House",
    };
    var categoryTools = {
      name: "Tools",
    };
    var categoryDelivery = {
      name: "Delivery",
    };

    Category.create(categoryCar, function (e) {
      if (e) {
        throw e;
      }
    });
    Category.create(categoryHouse, function (e) {
      if (e) {
        throw e;
      }
    });
    Category.create(categoryTools, function (e) {
      if (e) {
        throw e;
      }
    });
    Category.create(categoryDelivery, function (e) {
      if (e) {
        throw e;
      }
    });
  }
});

const getPostsByCategoryName = async (req, res, next) => {
  try {
    const posts = await Category.findById(req.params.name);
    res.status(200).send(posts);
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

module.exports = {
  getPostsByCategoryName,
};
