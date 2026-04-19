# Feeds and subscriptions

JGemini has preliminary support for subscribing to feeds. Its feed aggregator
collects posts from an arbitrary number of Atom or gmisub feeds, and presents
them in a single page, in date order.  It's reasonable
to ask JGemini to aggregate other aggregators. JGemini de-duplicates posts, so
you can subscribe to multiple sources of the same information, like the various
instances of Antenna that are presently available. 

The aggregator only includes posts which are dated within a particular time
period. The default is 14 days,
but you can change this using the [Settings dialog](settings_dialog.md)
or the [configuration file](config_file.md).  

## Adding feeds

To add a feed, navigate to the appropriate page and, whilst it's visible in the
document viewer, use the _Subscriptions|Subscribe to this page_ menu command. 

Anything that JGemini can treat as a feed will contain a list of links, each one
with a date in YYYY-MM-DD format at the start of the link text. It's not
a problem if the document contains other content, but JGemini only reads the
links.

It shouldn't harm JGemini if you subscribe to a page that is not, in fact,
recognizable as a feed. However, it won't achieve anything, and will slow
down the process of aggregation.

## Viewing the aggregated posts

To view the aggregated posts, use _Subscriptions|View aggregated posts_.  If
you've defined no feeds, or the aggregator hasn't yet been run, you'll see a
message to that effect.

The aggregator writes its results into a Gemtext file, by default named
`feeds_aggregated.gmi`.  This, again, can be changed in the configuration file.
When you view the aggregated posts, JGemini just shows the generated
Gemtext file, as it would any Gemtext file. You can see the posts themselves by
clicking the links on this page, as for any ordinary link.

## Editing and removing feeds

The feeds you add are stored in a simple Gemtext file, whose default name is
`feeds.gmi` in the `.jgemini` directory.  You can change this location in the
configuration file, if you wish.

You can add, re-order, or delete feeds in the subscriptions list using an
ordinary text editor (with JGemini not running), or using the built-in file
editor. To use the built-in editor, use the _Subscriptions|Edit..._ menu command. 
The editor is rudimentary, but using it saves you the trouble of finding the
file, and then restarting JGemini after editing it.

## Starting feed aggregation 

To run the feed aggregator, use _Subscriptions|Update subscriptions_.  The
aggregation process runs in a background thread, and can take a significant
time if you have many feeds, or some of the servers are slow to respond. You'll
see periodic status updates in the status bars of all open windows whilst it's
running, and an "Aggregation completed" message when it's finished.

For finer control and monitoring of the aggregation process, you can use the
[Feed aggregator dialog](feed_aggregator_dialog.md).  It's worth doing this
whenever you add a new feed, just to be sure its server is responding
correctly. This dialog doesn't do anything differently to _Update subscriptions_;
it just does it in a more visible way.

Once you're confident that feed aggregation is working correctly, you can
configure JGemini to do it every time it starts. Use the Feeds tab of
the [Settings dialog](settings_dialog.md) for this, or edit the
setting `feeds.update_on_startup` in the [configuration file](config_file.md).

## Supported feeds

JGemini supports feeds in Atom and "gmisub" format. It will subscribe to any
page of links in Gemtext format, provided each link's text begins with a date
in the form YYYY-MM-DD. The criteria for gmisub are not strict but, inclusive as
they are, there are still many capsules with pages of links that _don't_ match
the requirements, and won't be indexed as feeds.

[Documentation index](index.md)

 

