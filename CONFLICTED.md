# Things I'm still conflicted about

## Gemtext images

I remain unsure whether it should be the default to download images referenced
in a Gemtext file. I prefer it, but it isn't what other clients do.  
It's configurable, though.

Because image loading is asynchronous, you get to see the text before the
images are loaded, and it doesn't hurt to leave the page before the images are
shown. On the other hand, the asynchronous nature means that pages will
re-render, perhaps multiple times, after fetching images.

## `gophers` support

Currently there is none, and I'm not sure how much value there is in
implementing in. I'm not sure if there even is a robust way to implement it, as
gophermaps don't (so far as I know) provide any protocol information.

## finger, demarkus, scorpion, etc.

All these things would be quite easy to support, but I'm not sure there are
enough users to make it worthwhile.

