const Post = require("../Models/post_model");
const User = require("../Models/user_model");
const Sos = require("../Models/sos_model");
const Comment = require("../Models/comment_model");
const Category = require("../Models/category_model");
const router = require("../Routes/post_routes");

const getAllPosts = async (req, res, next) => {
  Post.find({}, function (err, docs) {
    if (err) console.log(err);
    else {
      res.status(200).send(docs);
    }
  });
};

/* ********************************************************** */

const getPostsById = async (req, res, next) => {
  try {
    const posts = await Post.findById(req.params.id);

    res.status(200).send(posts);
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

/* ********************************************************** */

const addPosts = async (req, res, next) => {
  var email = req.body.email;
  const user = await User.findOne({ email: email });
  var description = req.body.description;
  var title = req.body.title;
  var time = req.body.time;
  var role = req.body.role;
  var category = req.body.category;
  var photo = req.body.photo;
  // if (req.queryMap.photos == null) {
  photo = req.body.photo;
  // } else photo = req.queryMap.photos;

  const findCategory = await Category.findOne({ name: category });

  if (findCategory != null) {
    var friendsCircle;
    if (role == "SOS") friendsCircle = Number(req.body.circle);
    // else friendsCircle = 1;

    const post = await Post({
      sender: user,
      title: title,
      time: time,
      description: description,
      role: role,
      friends_circle: friendsCircle,
      category: category,
      photo: photo,
    });

    findCategory.save(async (error) => {
      if (error) {
        res.status(400).send({
          status: "fail",
          error: error.message,
        });
      } else {
        await Category.updateOne(
          { name: category },
          {
            $push: { posts: [post._id] },
          }
        );
      }
    });

    post.save((error, newPost) => {
      if (error) {
        res.status(400).send({
          status: "fail",
          error: error.message,
        });
      } else {
        res.status(200).send({
          status: "OK",
          _id: post._id,
        });
      }
    });
  }
};

const addPhotosToPost = async (req, res, next) => {
  try {
    const exists = await Post.updateOne(
      { _id: req.params.id },
      {
        $set: { photo: [] },
      }
    );
    exists = await Post.updateOne(
      { _id: req.params.id },
      {
        $push: { photo: req.body },
      }
    );

    const updatePost = await Post.findById(req.params.id);
    if (exists == null) return sendError(res, 400, "post does not exist");
    else {
      res.status(200).send({
        status: "OK",
        post: updatePost,
      });
    }
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

/* ********************************************************** */

const editPost = async (req, res, next) => {
  try {
    const exists = await Post.updateOne(
      { _id: req.params.id },
      {
        title: req.body.title,
        description: req.body.description,
        photo: req.body.photo,
        location: req.body.location,
        address: req.body.address,
      }
    );
    const updatePost = await Post.findById(req.params.id);
    if (exists == null) return sendError(res, 400, "post does not exist");
    else {
      res.status(200).send({
        status: "OK",
        _id: updatePost._id,
      });
    }
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

/* ********************************************************** */

const deletePost = async (req, res, next) => {
  try {
    // const exists = await Post.deleteOne({ _id: req.params.id });
    const exists = await Post.updateOne(
      { _id: req.params.id },
      { isDeleted: true }
    );

    const updatePost = await Post.findById(req.params.id);
    if (exists == null) return sendError(res, 400, "post does not exist");
    else {
      const findCategory = await Category.findOne({
        name: updatePost.category,
      });
      findCategory.save(async (error) => {
        if (error) {
          res.status(400).send({
            status: "fail",
            error: error.message,
          });
        } else {
          await Category.updateOne(
            { name: updatePost.category },
            {
              $pull: { posts: req.params.id },
            }
          );
        }
      });

      res.status(200).send({
        status: "OK",
        post: updatePost,
      });
    }
  } catch (err) {
    res.status(400).send({
      status: "fail",
      error: err.message,
    });
  }
};

/* ********************************************************** */

const getMyPosts = async (req, res, next) => {
  var sender = req.params.id;
  Post.find({ sender: sender }, function (err, docs) {
    if (err) console.log(err);
    else {
      res.status(200).send(docs);
    }
  });
};

module.exports = {
  getAllPosts,
  getPostsById,
  addPosts,
  getMyPosts,
  editPost,
  deletePost,
  addPhotosToPost,
};
