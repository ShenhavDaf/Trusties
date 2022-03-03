
const Post = require('../Models/post_model');

const getPosts = async (req, res, next) => {
    try{
        posts = await Post.find()
        res.status(200).send(posts);
    } catch (err) {
        res.status(400).send({
            'status': 'fail',
            'error': error.message
        });
    }
};

const getPostsById = async (req, res, next) => {
    try{
        posts = await Post.findById(req.params.id)
        res.status(200).send(posts);
    } catch (err) {
        res.status(400).send({
            'status': 'fail',
            'error': err.message
        });
    }
};

const addPosts = async (req, res, next) => {
    console.log('add new post: ' + req.body.message);
    sender = req.user.id;

    const post = Post({
        message: req.body.message,
        sender: sender
    });

    post.save((error, newPost) => {
        if(error) {
            res.status(400).send({
                'status': 'fail',
                'error': error.message
            });
        } else {
            res.status(200).send({
                'status': 'OK',
                'post': newPost
            });
        }
    });
}

module.exports = { 
    getPosts, 
    getPostsById,
    addPosts };